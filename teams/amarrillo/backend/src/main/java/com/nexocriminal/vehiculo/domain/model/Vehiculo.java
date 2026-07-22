package com.nexocriminal.vehiculo.domain.model;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;

import java.time.LocalDateTime;

/**
 * Modelo de dominio de Vehiculo. POJO puro, sin JPA ni Spring.
 * El propietario se referencia solo por id, para no acoplar este dominio
 * al de Persona.
 */
public class Vehiculo {

    private final Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private EstadoVehiculo estado;
    private Long propietarioId;
    private final LocalDateTime creadoEn;

    public Vehiculo(Long id, String placa, String marca, String modelo, Integer anio,
                    String color, EstadoVehiculo estado, Long propietarioId, LocalDateTime creadoEn) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.estado = estado;
        this.propietarioId = propietarioId;
        this.creadoEn = creadoEn;
    }

    /** Crea un vehiculo nuevo (sin id). Por defecto entra como ROBADO si no se especifica. */
    public static Vehiculo crear(String placa, String marca, String modelo, Integer anio,
                                 String color, EstadoVehiculo estado, Long propietarioId) {
        return new Vehiculo(
                null, placa, marca, modelo, anio, color,
                estado != null ? estado : EstadoVehiculo.ROBADO,
                propietarioId,
                LocalDateTime.now()
        );
    }

    /** Regla de negocio: actualiza datos editables (la placa no se cambia). */
    public void actualizarDatos(String marca, String modelo, Integer anio, String color,
                                EstadoVehiculo estado, Long propietarioId) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.estado = estado;
        this.propietarioId = propietarioId;
    }

    /** Regla de negocio: cambia el estado del vehiculo. */
    public void cambiarEstado(EstadoVehiculo nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public Long getId() { return id; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getAnio() { return anio; }
    public String getColor() { return color; }
    public EstadoVehiculo getEstado() { return estado; }
    public Long getPropietarioId() { return propietarioId; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}