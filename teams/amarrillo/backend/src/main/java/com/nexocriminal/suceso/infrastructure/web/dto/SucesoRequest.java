package com.nexocriminal.suceso.infrastructure.web.dto;

import com.nexocriminal.domain.suceso.TipoSuceso;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO de entrada para crear un suceso. El frontend envia las relaciones como
 * objetos anidados con id ({"id": X}), asi que las aceptamos en ese formato.
 */
public class SucesoRequest {

    /** Referencia a una entidad por id ({"id": X}). */
    public static class RefId {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    @NotNull
    private TipoSuceso tipo;

    @NotNull
    private LocalDateTime fechaHora;

    private String modusOperandi;
    private String descripcion;
    private RefId vehiculo;
    private RefId victima;
    private RefId ubicacion;
    private RefId ubicacionUltima;

    public TipoSuceso getTipo() { return tipo; }
    public void setTipo(TipoSuceso tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getModusOperandi() { return modusOperandi; }
    public void setModusOperandi(String modusOperandi) { this.modusOperandi = modusOperandi; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public RefId getVehiculo() { return vehiculo; }
    public void setVehiculo(RefId vehiculo) { this.vehiculo = vehiculo; }

    public RefId getVictima() { return victima; }
    public void setVictima(RefId victima) { this.victima = victima; }

    public RefId getUbicacion() { return ubicacion; }
    public void setUbicacion(RefId ubicacion) { this.ubicacion = ubicacion; }

    public RefId getUbicacionUltima() { return ubicacionUltima; }
    public void setUbicacionUltima(RefId ubicacionUltima) { this.ubicacionUltima = ubicacionUltima; }

    // Helpers para extraer ids de forma segura
    public Long vehiculoId() { return vehiculo != null ? vehiculo.getId() : null; }
    public Long victimaId() { return victima != null ? victima.getId() : null; }
    public Long ubicacionId() { return ubicacion != null ? ubicacion.getId() : null; }
    public Long ubicacionUltimaId() { return ubicacionUltima != null ? ubicacionUltima.getId() : null; }
}