package com.vacacionesclima.domain.port.in;

import com.vacacionesclima.domain.model.ConsultaClima;
import java.util.List;

// Puerto de ENTRADA
// Define las operaciones que el mundo exterior puede pedirle
// al sistema. Es una interfaz: define el QUE, no el COMO
public interface ConsultarClimaUseCase {

    // Consultar el clima de una ciudad y obtener recomendacion
    ConsultaClima consultarClima(String ciudad);

    // Obtener las ultimas 10 consultas realizadas
    List<ConsultaClima> obtenerHistorial();

    // Buscar consultas por nombre de ciudad
    List<ConsultaClima> buscarPorCiudad(String ciudad);
}