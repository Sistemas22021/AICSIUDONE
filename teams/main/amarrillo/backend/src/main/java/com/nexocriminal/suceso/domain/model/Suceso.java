package com.nexocriminal.suceso.domain.model;

import com.nexocriminal.domain.suceso.TipoSuceso;

import java.time.LocalDateTime;

/**
 * Modelo de dominio de Suceso. POJO puro. Las relaciones (vehiculo, victima,
 * ubicacion, ubicacionUltima) se guardan solo por id, para no acoplar este
 * dominio a los de vehiculo/persona/ubicacion.
 */
public class Suceso {

    private final Long id;
    private TipoSuceso tipo;
    private LocalDateTime fechaHora;
    private String modusOperandi;
    private String descripcion;
    private Long vehiculoId;
    private Long victimaId;
    private Long ubicacionId;
    private Long ubicacionUltimaId;
    private final LocalDateTime creadoEn;

    public Suceso(Long id, TipoSuceso tipo, LocalDateTime fechaHora, String modusOperandi,
                  String descripcion, Long vehiculoId, Long victimaId, Long ubicacionId,
                  Long ubicacionUltimaId, LocalDateTime creadoEn) {
        this.id = id;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.modusOperandi = modusOperandi;
        this.descripcion = descripcion;
        this.vehiculoId = vehiculoId;
        this.victimaId = victimaId;
        this.ubicacionId = ubicacionId;
        this.ubicacionUltimaId = ubicacionUltimaId;
        this.creadoEn = creadoEn;
    }

    /** Crea un suceso nuevo (sin id). */
    public static Suceso crear(TipoSuceso tipo, LocalDateTime fechaHora, String modusOperandi,
                               String descripcion, Long vehiculoId, Long victimaId,
                               Long ubicacionId, Long ubicacionUltimaId) {
        return new Suceso(
                null, tipo,
                fechaHora != null ? fechaHora : LocalDateTime.now(),
                modusOperandi, descripcion,
                vehiculoId, victimaId, ubicacionId, ubicacionUltimaId,
                LocalDateTime.now()
        );
    }

    /** Regla de negocio: este suceso implica que el vehiculo asociado fue robado. */
    public boolean esRoboConVehiculo() {
        return tipo == TipoSuceso.ROBO_VEHICULO && vehiculoId != null;
    }

    public Long getId() { return id; }
    public TipoSuceso getTipo() { return tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getModusOperandi() { return modusOperandi; }
    public String getDescripcion() { return descripcion; }
    public Long getVehiculoId() { return vehiculoId; }
    public Long getVictimaId() { return victimaId; }
    public Long getUbicacionId() { return ubicacionId; }
    public Long getUbicacionUltimaId() { return ubicacionUltimaId; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}