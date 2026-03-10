package com.vacacionesclima.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

// DTO de ENTRADA: representa el JSON que envía el frontend
// Ejemplo del JSON que llega:
// {
//     "ciudad": "Bogota"
// }
public class ClimaRequest {

    // @NotBlank valida que el campo no sea:
    // - null
    // - cadena vacía ""
    // - cadena con solo espacios "   "
    @NotBlank(message = "El nombre de la ciudad es obligatorio")
    private String ciudad;

    // Constructor vacío obligatorio para que Spring
    // pueda convertir el JSON a este objeto
    public ClimaRequest() {}

    // Constructor con parámetro
    public ClimaRequest(String ciudad) {
        this.ciudad = ciudad;
    }

    // Getter y Setter
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}