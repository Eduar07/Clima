package com.vacacionesclima.infrastructure.adapter.out.persistence;

import com.vacacionesclima.domain.model.ConsultaClima;
import com.vacacionesclima.domain.port.out.ClimaRepository;
import com.vacacionesclima.infrastructure.adapter.out.persistence.entity.ConsultaClimaEntity;
import com.vacacionesclima.infrastructure.adapter.out.persistence.repository.ConsultaClimaJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// @Component le dice a Spring que esta clase
// es un componente que debe gestionar
@Component

// Esta clase es el PUENTE entre el dominio y MySQL
// Implementa ClimaRepository (puerto del dominio)
// usando JPA para hablar con MySQL
// El dominio solo conoce ClimaRepository (la interfaz)
// nunca conoce esta clase directamente
public class ClimaRepositoryAdapter implements ClimaRepository {

    private final ConsultaClimaJpaRepository jpaRepository;

    // Inyeccion por constructor
    public ClimaRepositoryAdapter(
            ConsultaClimaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // =========================================================
    // GUARDAR
    // Convierte dominio a entidad JPA, guarda en MySQL
    // y convierte el resultado de vuelta a dominio
    // =========================================================
    @Override
    public ConsultaClima guardar(ConsultaClima consulta) {
        // Paso 1: convertir objeto del dominio a entidad JPA
        ConsultaClimaEntity entity = toEntity(consulta);

        // Paso 2: guardar en MySQL con JPA
        // save() hace el INSERT automaticamente
        ConsultaClimaEntity entityGuardada = jpaRepository.save(entity);

        // Paso 3: convertir entidad JPA de vuelta a dominio
        return toDomain(entityGuardada);
    }

    // =========================================================
    // BUSCAR POR ID
    // Optional evita NullPointerException si no existe
    // =========================================================
    @Override
    public Optional<ConsultaClima> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    // =========================================================
    // OBTENER ULTIMAS N CONSULTAS
    // =========================================================
    @Override
    public List<ConsultaClima> obtenerUltimas(int cantidad) {
        return jpaRepository
                .findTop10ByOrderByFechaConsultaDesc()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // =========================================================
    // BUSCAR POR CIUDAD
    // =========================================================
    @Override
    public List<ConsultaClima> buscarPorCiudad(String ciudad) {
        return jpaRepository
                .findByCiudadIgnoreCase(ciudad)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // =========================================================
    // METODO PRIVADO: Convierte dominio → entidad JPA
    // Se usa antes de guardar en MySQL
    // =========================================================
    private ConsultaClimaEntity toEntity(ConsultaClima domain) {
        ConsultaClimaEntity entity = new ConsultaClimaEntity();

        entity.setId(domain.getId());
        entity.setCiudad(domain.getCiudad());
        entity.setPais(domain.getPais());
        entity.setTemperatura(domain.getTemperatura());
        entity.setCondicionClimatica(domain.getCondicionClimatica());
        entity.setHumedad(domain.getHumedad());
        entity.setVelocidadViento(domain.getVelocidadViento());
        entity.setRecomendacion(domain.getRecomendacion());
        entity.setHayLluvia(domain.getHayLluvia());
        entity.setFechaConsulta(domain.getFechaConsulta());

        return entity;
    }

    // =========================================================
    // METODO PRIVADO: Convierte entidad JPA → dominio
    // Se usa después de traer datos de MySQL
    // =========================================================
    private ConsultaClima toDomain(ConsultaClimaEntity entity) {
        return new ConsultaClima(
                entity.getId(),
                entity.getCiudad(),
                entity.getPais(),
                entity.getTemperatura(),
                entity.getCondicionClimatica(),
                entity.getHumedad(),
                entity.getVelocidadViento(),
                entity.getRecomendacion(),
                entity.getHayLluvia(),
                entity.getFechaConsulta()
        );
    }
}