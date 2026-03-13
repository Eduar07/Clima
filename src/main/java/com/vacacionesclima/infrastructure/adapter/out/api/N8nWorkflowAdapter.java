package com.vacacionesclima.infrastructure.adapter.out.api;

import com.vacacionesclima.infrastructure.adapter.in.rest.dto.ClimaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter responsible for invoking the n8n workflow exposed through a webhook,
 * parsing the response, and translating it into the same DTO that the frontend expects.
 * Keeping this logic encapsulated makes it easier to switch URLs or response shapes later on.
 */
@Component
public class N8nWorkflowAdapter {

    private final RestTemplate restTemplate;
    private final String workflowUrl;

    public N8nWorkflowAdapter(RestTemplateBuilder restTemplateBuilder,
                              @Value("${n8n.workflow.url}") String workflowUrl,
                              @Value("${n8n.workflow.timeout-seconds:20}") long timeoutSeconds) {
        this.workflowUrl = workflowUrl;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(timeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    public ClimaResponse ejecutarWorkflow(String ciudad) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    workflowUrl,
                    Map.of("ciudad", ciudad),
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("n8n respondió sin cuerpo");
            }

            return mapearRespuesta(body);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al comunicarse con n8n: " + e.getMessage(), e);
        }
    }

    private ClimaResponse mapearRespuesta(Map<String, Object> body) {
        ClimaResponse respuesta = new ClimaResponse();
        respuesta.setCiudad(asString(body.get("ciudad")));
        respuesta.setPais(asString(body.get("pais")));
        respuesta.setTemperatura(asDouble(body.get("temperatura")));
        respuesta.setCondicionClimatica(
                asString(Optional.ofNullable(body.get("condicionClimatica"))
                        .orElse(body.get("descripcion")))
        );
        respuesta.setHumedad(asInteger(body.get("humedad")));
        respuesta.setVelocidadViento(asDouble(body.get("velocidadViento")));
        respuesta.setHayLluvia(asBoolean(body.get("hayLluvia")));
        respuesta.setRecomendacion(asString(body.get("recomendacion")));
        respuesta.setMensaje(asString(body.get("mensaje")));
        respuesta.setExitoso(asBoolean(body.get("exitoso")) != Boolean.FALSE);

        if (respuesta.getCondicionClimatica() == null) {
            respuesta.setCondicionClimatica("Descripción no disponible");
        }
        if (respuesta.getRecomendacion() == null) {
            respuesta.setRecomendacion("n8n no envió recomendación.");
        }
        if (respuesta.getTemperatura() == null) {
            respuesta.setTemperatura(0.0);
        }

        return respuesta;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Double asDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        return "true".equalsIgnoreCase(value.toString());
    }
}
