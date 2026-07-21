package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de solicitud para crear una escena de levantamiento asociada a un expediente.
 * Contiene expedienteId y levantadaPorId (investigador responsable).
 */
public record EscenaRequest(
        @NotNull(message = "El expediente es obligatorio")
        Long expedienteId,

        @NotNull(message = "El investigador responsable es obligatorio")
        UUID levantadaPorId
) {
    public Long getExpedienteId() { return this.expedienteId; }
    public UUID getLevantadaPorId() { return this.levantadaPorId; }
}
