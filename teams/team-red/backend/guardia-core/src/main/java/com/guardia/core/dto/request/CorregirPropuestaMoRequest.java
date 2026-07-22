package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Payload para corregir uno o más campos de clasificación de la propuesta de
 * MO generada por la IA (HU3, CA2). La justificación es obligatoria (CA3).
 * Los campos de clasificación son opcionales de forma individual: solo se
 * sobreescribe lo que el Analista efectivamente envía distinto de null.
 */
public record CorregirPropuestaMoRequest(
        @NotNull(message = "Debe indicar el analista que corrige la propuesta.")
        UUID analistaId,

        String caracteristicasComunes,
        String posibleFirma,
        String consistenciaHorarioZona,

        @NotBlank(message = "La justificación es obligatoria al corregir una propuesta.")
        String justificacion
) {}