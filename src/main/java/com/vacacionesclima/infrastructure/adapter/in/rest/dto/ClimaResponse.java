package com.vacacionesclima.infrastructure.adapter.in.rest.dto;

// DTO de SALIDA: representa el JSON que el backend
// devuelve al frontend después de consultar el clima
// Ejemplo del JSON que sale:
// {
//     "ciudad": "Bogota",
//     "pais": "CO",
//     "temperatura": 14.5,
//     "condicionClimatica": "nublado",
//     "humedad": 80,
//     "velocidadViento": 15.0,
//     "hayLluvia": false,
//     "recomendacion": "Usa chaqueta o sueter.",
//     "exitoso": true
// }
public class ClimaResponse {

    private String ciudad;
    private String pais;
    private Double temperatura;
    private String condicionClimatica;
    private Integer humedad;
    private Double velocidadViento;
    private Boolean hayLluvia;
    private String recomendacion;
    private String mensaje;
    private boolean exitoso;

    // Constructor vacío
    public ClimaResponse() {}

    // Constructor para respuesta exitosa
    public ClimaResponse(String ciudad, String pais,
                         Double temperatura, String condicionClimatica,
                         Integer humedad, Double velocidadViento,
                         Boolean hayLluvia, String recomendacion) {
        this.ciudad = ciudad;
        this.pais = pais;
        this.temperatura = temperatura;
        this.condicionClimatica = condicionClimatica;
        this.humedad = humedad;
        this.velocidadViento = velocidadViento;
        this.hayLluvia = hayLluvia;
        this.recomendacion = recomendacion;
        this.exitoso = true;
    }

    // Getters y Setters
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public String getCondicionClimatica() { return condicionClimatica; }
    public void setCondicionClimatica(String condicionClimatica) {
        this.condicionClimatica = condicionClimatica;
    }

    public Integer getHumedad() { return humedad; }
    public void setHumedad(Integer humedad) { this.humedad = humedad; }

    public Double getVelocidadViento() { return velocidadViento; }
    public void setVelocidadViento(Double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }

    public Boolean getHayLluvia() { return hayLluvia; }
    public void setHayLluvia(Boolean hayLluvia) {
        this.hayLluvia = hayLluvia;
    }

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public boolean isExitoso() { return exitoso; }
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
}