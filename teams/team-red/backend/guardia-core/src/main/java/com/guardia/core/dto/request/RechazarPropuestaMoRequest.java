package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Payload para rechazar la propuesta de la IA e ingresar una clasificación
 * manual (HU3, CA2). Justificación obligatoria (CA3).
 */
public record RechazarPropuestaMoRequest(
        @NotNull(message = "Debe indicar el analista que rechaza la propuesta.")
        UUID analistaId,

        @NotBlank(message = "Debe ingresar la clasificación manual del Modus Operandi.")
        String clasificacionManual,

        @NotBlank(message = "La justificación es obligatoria al rechazar una propuesta.")
        String justificacion
) {}