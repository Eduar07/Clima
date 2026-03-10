package com.vacacionesclima.domain.port.out;

import com.vacacionesclima.domain.model.DatosClima;

// Puerto de SALIDA para la API externa del clima
// El dominio no sabe si usamos OpenWeatherMap,
// AccuWeather u otra API. Solo define el contrato
public interface ClimaApiPort {

    // Obtener los datos del clima de una ciudad
    // Lanza RuntimeException si la ciudad no existe
    DatosClima obtenerDatosClima(String ciudad);
}