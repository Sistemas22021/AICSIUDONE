package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;


/**
 * Payload para aprobar tal cual la propuesta de MO generada por la IA (HU3, CA2).
 */
public record AprobarPropuestaMoRequest(
        @NotNull(message = "Debe indicar el analista que aprueba la propuesta.")
        UUID analistaId
) {}
