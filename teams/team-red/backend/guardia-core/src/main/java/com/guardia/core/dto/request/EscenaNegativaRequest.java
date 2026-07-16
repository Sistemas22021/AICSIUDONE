package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para registrar una escena negativa (sin hallazgos).
 * Campos: elemento buscado, área, resultado y observaciones, vinculado a una escena.
 */
public record EscenaNegativaRequest(
        @NotBlank(message = "El elemento buscado es obligatorio")
        String elementoBuscado,

        @NotBlank(message = "El área inspeccionada es obligatoria")
        String areaInspeccionada,

        String resultado,
        String observacion,

        @NotNull(message = "La escena es obligatoria")
        Long escenaId,
        Boolean sinElementosNegativos
) {
    public String getElementoBuscado() { return this.elementoBuscado; }
    public String getAreaInspeccionada() { return this.areaInspeccionada; }
    public String getResultado() { return this.resultado; }
    public String getObservacion() { return this.observacion; }
    public Long getEscenaId() { return this.escenaId; }
    public Boolean getSinElementosNegativos() { return this.sinElementosNegativos; }
}
