package com.vacacionesclima.domain.model;

import java.time.LocalDateTime;

// Entidad principal del dominio
// Java puro: sin imports de Spring, JPA ni MySQL
public class ConsultaClima {

    private Long id;
    private String ciudad;
    private String pais;
    private Double temperatura;
    private String condicionClimatica;
    private Integer humedad;
    private Double velocidadViento;
    private String recomendacion;
    private Boolean hayLluvia;
    private LocalDateTime fechaConsulta;

    // Constructor vacío
    public ConsultaClima() {}

    // Constructor completo
    public ConsultaClima(Long id, String ciudad, String pais,
                         Double temperatura, String condicionClimatica,
                         Integer humedad, Double velocidadViento,
                         String recomendacion, Boolean hayLluvia,
                         LocalDateTime fechaConsulta) {
        this.id = id;
        this.ciudad = ciudad;
        this.pais = pais;
        this.temperatura = temperatura;
        this.condicionClimatica = condicionClimatica;
        this.humedad = humedad;
        this.velocidadViento = velocidadViento;
        this.recomendacion = recomendacion;
        this.hayLluvia = hayLluvia;
        this.fechaConsulta = fechaConsulta;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

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

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public Boolean getHayLluvia() { return hayLluvia; }
    public void setHayLluvia(Boolean hayLluvia) { this.hayLluvia = hayLluvia; }

    public LocalDateTime getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }
}