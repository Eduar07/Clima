package com.vacacionesclima.domain.port.out;

import com.vacacionesclima.domain.model.ConsultaClima;
import java.util.List;
import java.util.Optional;

// Puerto de SALIDA para persistencia
// El dominio no sabe si los datos se guardan en MySQL,
// MongoDB o cualquier otro sistema. Solo sabe que
// alguien implementara estos metodos
public interface ClimaRepository {

    // Guardar una consulta en el almacenamiento
    ConsultaClima guardar(ConsultaClima consulta);

    // Buscar una consulta por su ID
    // Optional evita el NullPointerException si no existe
    Optional<ConsultaClima> buscarPorId(Long id);

    // Obtener las ultimas N consultas ordenadas por fecha
    List<ConsultaClima> obtenerUltimas(int cantidad);

    // Buscar todas las consultas de una ciudad
    List<ConsultaClima> buscarPorCiudad(String ciudad);
}