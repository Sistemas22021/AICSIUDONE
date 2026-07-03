package com.nexocriminal.vehiculo.infrastructure.web.dto;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;

import java.time.LocalDateTime;

/** DTO de salida. Incluye el propietario anidado (resumen) para no romper el frontend. */
public class VehiculoResponse {

    private final Long id;
    private final String placa;
    private final String marca;
    private final String modelo;
    private final Integer anio;
    private final String color;
    private final EstadoVehiculo estado;
    private final PropietarioResumen propietario;
    private final LocalDateTime creadoEn;

    public VehiculoResponse(Vehiculo v, PropietarioResumen propietario) {
        this.id = v.getId();
        this.placa = v.getPlaca();
        this.marca = v.getMarca();
        this.modelo = v.getModelo();
        this.anio = v.getAnio();
        this.color = v.getColor();
        this.estado = v.getEstado();
        this.propietario = propietario;
        this.creadoEn = v.getCreadoEn();
    }

    public Long getId() { return id; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getAnio() { return anio; }
    public String getColor() { return color; }
    public EstadoVehiculo getEstado() { return estado; }
    public PropietarioResumen getPropietario() { return propietario; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}