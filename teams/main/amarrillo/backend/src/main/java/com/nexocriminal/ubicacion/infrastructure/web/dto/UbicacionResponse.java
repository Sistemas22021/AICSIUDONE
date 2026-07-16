package com.nexocriminal.ubicacion.infrastructure.web.dto;

import com.nexocriminal.domain.ubicacion.TipoUbicacion;
import com.nexocriminal.ubicacion.domain.model.Ubicacion;

import java.time.LocalDateTime;

/** DTO de salida: lo que el cliente recibe. Se construye desde el modelo de dominio. */
public class UbicacionResponse {

    private final Long id;
    private final String direccion;
    private final double latitud;
    private final double longitud;
    private final TipoUbicacion tipo;
    private final boolean nodoSospechoso;
    private final LocalDateTime creadoEn;

    public UbicacionResponse(Ubicacion u) {
        this.id = u.getId();
        this.direccion = u.getDireccion();
        this.latitud = u.getLatitud();
        this.longitud = u.getLongitud();
        this.tipo = u.getTipo();
        this.nodoSospechoso = u.isNodoSospechoso();
        this.creadoEn = u.getCreadoEn();
    }

    public Long getId() { return id; }
    public String getDireccion() { return direccion; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public TipoUbicacion getTipo() { return tipo; }
    public boolean isNodoSospechoso() { return nodoSospechoso; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}