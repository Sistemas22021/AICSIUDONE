package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Payload para marcar una alerta de patrón de MO como descartada
 * El motivo es opcional: documenta por qué el patrón no ameritaba seguimiento.
 */
public record DescartarAlertaRequest(
        @NotNull(message = "Debe indicar el usuario que descarta la alerta.")
        UUID usuarioId,

        String motivo
) {}