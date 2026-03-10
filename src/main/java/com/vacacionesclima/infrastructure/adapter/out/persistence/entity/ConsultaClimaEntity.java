package com.vacacionesclima.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// @Entity le dice a JPA que esta clase representa
// una tabla en la base de datos MySQL
@Entity

// @Table especifica el nombre exacto de la tabla
// que creamos en el archivo schema.sql
@Table(name = "consultas_clima")
public class ConsultaClimaEntity {

    // @Id indica que este campo es la llave primaria
    // @GeneratedValue con IDENTITY usa el AUTO_INCREMENT de MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column mapea el atributo Java a la columna MySQL
    // nullable = false significa que NO puede ser nulo
    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 10)
    private String pais;

    @Column(nullable = false)
    private Double temperatura;

    // name = "condicion_climatica" indica el nombre exacto
    // de la columna en MySQL (con guion bajo)
    // En Java usamos camelCase: condicionClimatica
    @Column(name = "condicion_climatica",
            nullable = false, length = 200)
    private String condicionClimatica;

    private Integer humedad;

    @Column(name = "velocidad_viento")
    private Double velocidadViento;

    // columnDefinition = "TEXT" para textos largos
    @Column(nullable = false, columnDefinition = "TEXT")
    private String recomendacion;

    @Column(name = "hay_lluvia")
    private Boolean hayLluvia = false;

    @Column(name = "fecha_consulta")
    private LocalDateTime fechaConsulta;

    @Column(name = "ip_usuario", length = 45)
    private String ipUsuario;

    // @PrePersist se ejecuta automaticamente ANTES
    // de insertar el registro en la base de datos
    // Asigna la fecha y hora actual si no fue asignada
    @PrePersist
    public void prePersist() {
        if (this.fechaConsulta == null) {
            this.fechaConsulta = LocalDateTime.now();
        }
    }

    // Constructor vacío obligatorio para JPA
    public ConsultaClimaEntity() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public Boolean getHayLluvia() { return hayLluvia; }
    public void setHayLluvia(Boolean hayLluvia) {
        this.hayLluvia = hayLluvia;
    }

    public LocalDateTime getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getIpUsuario() { return ipUsuario; }
    public void setIpUsuario(String ipUsuario) {
        this.ipUsuario = ipUsuario;
    }
}