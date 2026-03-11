package com.vacacionesclima.infrastructure.config;

import com.vacacionesclima.application.usecase.ConsultarClimaUseCaseImpl;
import com.vacacionesclima.domain.port.in.ConsultarClimaUseCase;
import com.vacacionesclima.domain.port.out.ClimaApiPort;
import com.vacacionesclima.domain.port.out.ClimaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration le dice a Spring que esta clase
// contiene definiciones de Beans (objetos que Spring gestiona)
@Configuration
public class BeanConfiguration {

    // @Bean le dice a Spring:
    // "cuando alguien necesite un ConsultarClimaUseCase,
    // usa este metodo para crearlo"
    //
    // Spring inyecta automaticamente los parametros:
    // - climaApiPort     → OpenWeatherMapAdapter
    // - climaRepository  → ClimaRepositoryAdapter
    //
    // Esto conecta todo el sistema:
    // Controller → UseCase → ApiPort/Repository → MySQL/API
    @Bean
    public ConsultarClimaUseCase consultarClimaUseCase(
            ClimaApiPort climaApiPort,
            ClimaRepository climaRepository) {

        return new ConsultarClimaUseCaseImpl(
                climaApiPort,
                climaRepository
        );
    }
}