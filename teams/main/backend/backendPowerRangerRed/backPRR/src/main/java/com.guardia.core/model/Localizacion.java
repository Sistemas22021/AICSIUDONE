package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "localizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Localizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String municipio;

    @Column
    private String sector;

    @Column
    private String direccion;

    @Column
    private String referencia;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    // Methods
    public void registrarGPS(Double lat, Double lon) {
        this.latitud = lat;
        this.longitud = lon;
    }

    public void registrarDireccionManual(String municipio, String sector,
                                         String direccion, String referencia) {
        this.municipio = municipio;
        this.sector = sector;
        this.direccion = direccion;
        this.referencia = referencia;
    }

    public boolean validarUbicacion() {
        return (this.latitud != null && this.longitud != null)
                || (this.direccion != null && !this.direccion.isBlank());
    }

    public String obtenerResumenUbicacion() {
        if (this.municipio != null && this.sector != null) {
            return this.municipio + ", " + this.sector + " - " + this.direccion;
        }
        if (this.latitud != null && this.longitud != null) {
            return "GPS: " + this.latitud + ", " + this.longitud;
        }
        return "Ubicación no especificada";
    }

    // Explicit accessors
    public Long getId() { return this.id; }
    public String getMunicipio() { return this.municipio; }
    public String getSector() { return this.sector; }
    public String getDireccion() { return this.direccion; }
    public String getReferencia() { return this.referencia; }
    public Double getLatitud() { return this.latitud; }
    public Double getLongitud() { return this.longitud; }

    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public void setSector(String sector) { this.sector = sector; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}

