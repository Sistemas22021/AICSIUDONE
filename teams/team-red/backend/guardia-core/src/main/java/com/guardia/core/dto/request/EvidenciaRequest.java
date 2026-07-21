package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
/**
 * DTO de solicitud para registrar una evidencia.
 * Campos: numeroItem, tipo, descripcion, escenaId, investigadorId y hash enviado por cliente.
 */
public record EvidenciaRequest(
        String numeroItem,

        @NotBlank(message = "El tipo de evidencia es obligatorio")
        String tipo,

        String descripcion,

        @NotNull(message = "La escena es obligatoria")
        Long escenaId,

        UUID investigadorId,

        String hashArchivoCliente
) {
    public String getNumeroItem() { return this.numeroItem; }
    public String getTipo() { return this.tipo; }
    public String getDescripcion() { return this.descripcion; }
    public Long getEscenaId() { return this.escenaId; }
    public UUID getInvestigadorId() { return this.investigadorId; }
}

