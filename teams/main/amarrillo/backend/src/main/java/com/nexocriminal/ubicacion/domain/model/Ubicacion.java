package com.nexocriminal.ubicacion.domain.model;

import com.nexocriminal.domain.ubicacion.TipoUbicacion;

import java.time.LocalDateTime;

/**
 * Modelo de dominio de Ubicacion. POJO puro: no depende de Spring ni de JPA.
 * Contiene los datos y el comportamiento de negocio de una ubicacion.
 */
public class Ubicacion {

    private final Long id;
    private String direccion;
    private double latitud;
    private double longitud;
    private TipoUbicacion tipo;
    private boolean nodoSospechoso;
    private final LocalDateTime creadoEn;

    public Ubicacion(Long id, String direccion, double latitud, double longitud,
                     TipoUbicacion tipo, boolean nodoSospechoso, LocalDateTime creadoEn) {
        this.id = id;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo;
        this.nodoSospechoso = nodoSospechoso;
        this.creadoEn = creadoEn;
    }

    /** Crea una ubicacion nueva (sin id, aun no persistida). */
    public static Ubicacion crear(String direccion, double latitud, double longitud, TipoUbicacion tipo) {
        return new Ubicacion(
                null,
                direccion,
                latitud,
                longitud,
                tipo != null ? tipo : TipoUbicacion.OTRO,
                false,
                LocalDateTime.now()
        );
    }

    /** Regla de negocio: marca esta ubicacion como nodo sospechoso. */
    public void marcarComoNodoSospechoso() {
        this.nodoSospechoso = true;
    }

    /** Regla de negocio: actualiza los datos editables de la ubicacion. */
    public void actualizarDatos(String direccion, double latitud, double longitud, TipoUbicacion tipo) {
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo != null ? tipo : TipoUbicacion.OTRO;
    }

    public Long getId() { return id; }
    public String getDireccion() { return direccion; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public TipoUbicacion getTipo() { return tipo; }
    public boolean isNodoSospechoso() { return nodoSospechoso; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}