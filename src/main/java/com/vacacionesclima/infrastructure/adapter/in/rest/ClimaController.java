package com.vacacionesclima.infrastructure.adapter.in.rest;

import com.vacacionesclima.domain.model.ConsultaClima;
import com.vacacionesclima.domain.port.in.ConsultarClimaUseCase;
import com.vacacionesclima.domain.port.out.ClimaRepository;
import com.vacacionesclima.infrastructure.adapter.in.rest.dto.ClimaRequest;
import com.vacacionesclima.infrastructure.adapter.in.rest.dto.ClimaResponse;
import com.vacacionesclima.infrastructure.adapter.in.rest.mapper.ClimaMapper;
import com.vacacionesclima.infrastructure.adapter.out.api.N8nWorkflowAdapter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// @RestController indica que esta clase maneja
// peticiones HTTP y devuelve JSON automáticamente
@RestController

// @RequestMapping define el prefijo de todas las URLs
// Todas las rutas de este controlador empiezan con /api/clima
@RequestMapping("/api/clima")

// @CrossOrigin permite que el frontend pueda llamar
// al backend aunque estén en puertos diferentes
// origins = "*" acepta peticiones de cualquier origen
@CrossOrigin(origins = "*")
public class ClimaController {

    // Usamos el PUERTO (interfaz) no la implementación
    // Esto permite cambiar la implementación sin tocar
    // el controlador
    private final ConsultarClimaUseCase consultarClimaUseCase;
    private final ClimaMapper mapper;
    private final ClimaRepository climaRepository;
    private final N8nWorkflowAdapter n8nWorkflowAdapter;

    // Inyección por constructor
    public ClimaController(ConsultarClimaUseCase consultarClimaUseCase,
                           ClimaMapper mapper,
                           ClimaRepository climaRepository,
                           N8nWorkflowAdapter n8nWorkflowAdapter) {
        this.consultarClimaUseCase = consultarClimaUseCase;
        this.mapper = mapper;
        this.climaRepository = climaRepository;
        this.n8nWorkflowAdapter = n8nWorkflowAdapter;
    }

    // =========================================================
    // ENDPOINT 1: Consultar clima de una ciudad
    // Método:  POST
    // URL:     /api/clima/consultar
    // Body:    { "ciudad": "Bogota" }
    // =========================================================
    @PostMapping("/consultar")
    public ResponseEntity<ClimaResponse> consultarClima(
            @Valid @RequestBody ClimaRequest request) {

        try {
            // Llamar al caso de uso con la ciudad recibida
            ConsultaClima resultado = consultarClimaUseCase
                    .consultarClima(request.getCiudad().trim());

            // Convertir resultado del dominio a DTO de respuesta
            ClimaResponse response = mapper.toResponse(resultado);

            // HTTP 200 OK
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Si la ciudad no existe o hay error en la API
            ClimaResponse error = new ClimaResponse();
            error.setExitoso(false);
            error.setMensaje("No se pudo obtener el clima de: "
                    + request.getCiudad()
                    + ". Verifica que el nombre sea correcto.");

            // HTTP 404 Not Found
            return ResponseEntity.status(404).body(error);
        }
    }

    // =========================================================
    // ENDPOINT EXTRA: Consultar clima usando n8n como backend
    // Método: POST
    // URL:    /api/clima/consultar-n8n
    // Este endpoint actúa como proxy contra n8n para evitar
    // problemas de CORS desde el navegador.
    // =========================================================
    @PostMapping("/consultar-n8n")
    public ResponseEntity<ClimaResponse> consultarClimaConN8n(
            @Valid @RequestBody ClimaRequest request) {

        try {
            ClimaResponse respuesta = n8nWorkflowAdapter
                    .ejecutarWorkflow(request.getCiudad().trim());

            registrarConsulta(respuesta);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            ClimaResponse error = new ClimaResponse();
            error.setExitoso(false);
            error.setMensaje("No se pudo comunicar con n8n. " +
                    "Detalle: " + e.getMessage());
            return ResponseEntity.status(502).body(error);
        }
    }

    private void registrarConsulta(ClimaResponse respuesta) {
        if (respuesta == null || !respuesta.isExitoso()) {
            return;
        }

        ConsultaClima consulta = new ConsultaClima();
        consulta.setCiudad(respuesta.getCiudad());
        consulta.setPais(respuesta.getPais());
        consulta.setTemperatura(
                respuesta.getTemperatura() != null
                        ? respuesta.getTemperatura()
                        : 0.0
        );
        consulta.setCondicionClimatica(
                respuesta.getCondicionClimatica() != null
                        ? respuesta.getCondicionClimatica()
                        : "Descripción no disponible"
        );
        consulta.setHumedad(respuesta.getHumedad());
        consulta.setVelocidadViento(respuesta.getVelocidadViento());
        consulta.setRecomendacion(
                respuesta.getRecomendacion() != null
                        ? respuesta.getRecomendacion()
                        : "n8n no envió recomendación."
        );
        consulta.setHayLluvia(respuesta.getHayLluvia());
        consulta.setFechaConsulta(LocalDateTime.now());

        climaRepository.guardar(consulta);
    }

    @PostMapping("/guardar")
    public ResponseEntity<ClimaResponse> guardar(
            @RequestBody ClimaRequest request) {
        try {
            ConsultaClima consulta = new ConsultaClima();
            consulta.setCiudad(request.getCiudad());
            climaRepository.guardar(consulta);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // =========================================================
    // ENDPOINT 2: Obtener historial de consultas
    // Método:  GET
    // URL:     /api/clima/historial
    // =========================================================
    @GetMapping("/historial")
    public ResponseEntity<List<ClimaResponse>> obtenerHistorial() {

        List<ClimaResponse> historial = consultarClimaUseCase
                .obtenerHistorial()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }

    // =========================================================
    // ENDPOINT 3: Buscar consultas por ciudad
    // Método:  GET
    // URL:     /api/clima/ciudad/{nombre}
    // Ejemplo: /api/clima/ciudad/Bogota
    // =========================================================
    @GetMapping("/ciudad/{nombre}")
    public ResponseEntity<List<ClimaResponse>> buscarPorCiudad(
            @PathVariable String nombre) {

        List<ClimaResponse> consultas = consultarClimaUseCase
                .buscarPorCiudad(nombre)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(consultas);
    }

    // =========================================================
    // ENDPOINT 4: Verificar que el servidor está funcionando
    // Método:  GET
    // URL:     /api/clima/ping
    // =========================================================
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(
            "Vacaciones-Clima funcionando OK ✅"
        );
    }
}
