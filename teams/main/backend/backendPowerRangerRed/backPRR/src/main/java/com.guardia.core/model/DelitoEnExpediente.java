package com.guardia.core.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
/**
 * Embeddable que representa un delito embebido dentro de un expediente (legacy).
 * Contiene subtipo y marca temporal del hecho.
 */
public class DelitoEnExpediente {

    private String subtipoDelito; // Ej: "HOMICIDIO_CULPOSO", "DAÑO_PROPIEDAD", "CONDUCCION_EBRIEDAD"
    private LocalDateTime fechaHoraHecho; // La hora específica en la que ocurrió este delito en particular

    public DelitoEnExpediente() {}

    public String getSubtipoDelito() { return this.subtipoDelito; }
    public void setSubtipoDelito(String subtipoDelito) { this.subtipoDelito = subtipoDelito; }

    public LocalDateTime getFechaHoraHecho() { return this.fechaHoraHecho; }
    public void setFechaHoraHecho(LocalDateTime fechaHoraHecho) { this.fechaHoraHecho = fechaHoraHecho; }
}