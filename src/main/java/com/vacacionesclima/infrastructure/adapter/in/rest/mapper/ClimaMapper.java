package com.vacacionesclima.infrastructure.adapter.in.rest.mapper;

import com.vacacionesclima.domain.model.ConsultaClima;
import com.vacacionesclima.infrastructure.adapter.in.rest.dto.ClimaResponse;
import org.springframework.stereotype.Component;

// Mapper: convierte objetos del dominio a DTOs
// Separa la lógica de conversión del controlador
// para mantener el código limpio y organizado
@Component
public class ClimaMapper {

    // Convierte una entidad del dominio ConsultaClima
    // al DTO ClimaResponse que se envía al frontend
    public ClimaResponse toResponse(ConsultaClima domain) {

        ClimaResponse response = new ClimaResponse();

        response.setCiudad(domain.getCiudad());
        response.setPais(domain.getPais());
        response.setTemperatura(domain.getTemperatura());
        response.setCondicionClimatica(domain.getCondicionClimatica());
        response.setHumedad(domain.getHumedad());
        response.setVelocidadViento(domain.getVelocidadViento());
        response.setHayLluvia(domain.getHayLluvia());
        response.setRecomendacion(domain.getRecomendacion());
        response.setExitoso(true);

        return response;
    }
}