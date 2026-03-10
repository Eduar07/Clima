package com.vacacionesclima.infrastructure.adapter.out.persistence.repository;

import com.vacacionesclima.infrastructure.adapter.out.persistence.entity.ConsultaClimaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// @Repository le dice a Spring que esta interfaz
// maneja operaciones con la base de datos
@Repository

// JpaRepository<ConsultaClimaEntity, Long> significa:
// - ConsultaClimaEntity = la entidad que maneja
// - Long = el tipo del campo ID
// Spring genera automaticamente estos metodos sin escribir SQL:
// - save()       → INSERT o UPDATE en MySQL
// - findById()   → SELECT WHERE id = ?
// - findAll()    → SELECT * FROM consultas_clima
// - delete()     → DELETE WHERE id = ?
// - count()      → SELECT COUNT(*) FROM consultas_clima
public interface ConsultaClimaJpaRepository
        extends JpaRepository<ConsultaClimaEntity, Long> {

    // Spring genera el SQL automaticamente basandose
    // en el nombre del metodo:
    // findByCiudadIgnoreCase →
    // SELECT * FROM consultas_clima
    // WHERE UPPER(ciudad) = UPPER(?)
    List<ConsultaClimaEntity> findByCiudadIgnoreCase(String ciudad);

    // findTop10ByOrderByFechaConsultaDesc →
    // SELECT * FROM consultas_clima
    // ORDER BY fecha_consulta DESC
    // LIMIT 10
    List<ConsultaClimaEntity> findTop10ByOrderByFechaConsultaDesc();
}