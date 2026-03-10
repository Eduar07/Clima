package com.vacacionesclima.domain.model;

// Value Object: representa los datos crudos que vienen de la API
// Es inmutable: todos los atributos son final y no tiene setters
public class DatosClima {

    private final String ciudad;
    private final String pais;
    private final Double temperatura;
    private final String descripcion;
    private final Integer humedad;
    private final Double velocidadViento;
    private final Boolean hayLluvia;

    // Constructor completo
    public DatosClima(String ciudad, String pais,
                      Double temperatura, String descripcion,
                      Integer humedad, Double velocidadViento,
                      Boolean hayLluvia) {
        this.ciudad = ciudad;
        this.pais = pais;
        this.temperatura = temperatura;
        this.descripcion = descripcion;
        this.humedad = humedad;
        this.velocidadViento = velocidadViento;
        this.hayLluvia = hayLluvia;
    }

    // Solo Getters (sin setters porque es inmutable)
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }
    public Double getTemperatura() { return temperatura; }
    public String getDescripcion() { return descripcion; }
    public Integer getHumedad() { return humedad; }
    public Double getVelocidadViento() { return velocidadViento; }
    public Boolean getHayLluvia() { return hayLluvia; }
}