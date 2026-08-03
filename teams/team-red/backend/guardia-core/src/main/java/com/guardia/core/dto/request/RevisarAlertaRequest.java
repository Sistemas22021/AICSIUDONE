package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Payload para marcar una alerta de patrón de MO como revisada
 */
public record RevisarAlertaRequest(
        @NotNull(message = "Debe indicar el usuario que revisa la alerta.")
        UUID usuarioId
) {}