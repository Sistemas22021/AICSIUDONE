package com.nexocriminal.vehiculo.infrastructure.web.dto;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.validacion.anotaciones.PlacaVenezolana;
import jakarta.validation.constraints.NotBlank;

/** DTO de entrada para crear/actualizar vehiculo. */
public class VehiculoRequest {

    @NotBlank
    @PlacaVenezolana
    private String placa;

    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    private Integer anio;
    private String color;
    private EstadoVehiculo estado;
    private Long propietarioId;

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public EstadoVehiculo getEstado() { return estado; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }

    public Long getPropietarioId() { return propietarioId; }
    public void setPropietarioId(Long propietarioId) { this.propietarioId = propietarioId; }
}