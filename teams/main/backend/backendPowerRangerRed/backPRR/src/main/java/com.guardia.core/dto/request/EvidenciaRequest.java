package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvidenciaRequest(
        String numeroItem,

        @NotBlank(message = "El tipo de evidencia es obligatorio")
        String tipo,

        String descripcion,

        @NotNull(message = "La escena es obligatoria")
        Long escenaId,

        Long investigadorId
) {
    public String getNumeroItem() { return this.numeroItem; }
    public String getTipo() { return this.tipo; }
    public String getDescripcion() { return this.descripcion; }
    public Long getEscenaId() { return this.escenaId; }
    public Long getInvestigadorId() { return this.investigadorId; }
}

