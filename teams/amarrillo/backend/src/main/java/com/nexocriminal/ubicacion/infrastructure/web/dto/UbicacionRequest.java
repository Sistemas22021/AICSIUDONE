package com.nexocriminal.ubicacion.infrastructure.web.dto;

import com.nexocriminal.domain.ubicacion.TipoUbicacion;
import jakarta.validation.constraints.NotNull;

/** DTO de entrada: lo que el cliente envia al crear/actualizar una ubicacion. */
public class UbicacionRequest {

    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    private TipoUbicacion tipo;

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public TipoUbicacion getTipo() { return tipo; }
    public void setTipo(TipoUbicacion tipo) { this.tipo = tipo; }
}