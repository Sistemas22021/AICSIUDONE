package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;

public record EscenaRequest(
        @NotNull(message = "El expediente es obligatorio")
        Long expedienteId,

        @NotNull(message = "El investigador responsable es obligatorio")
        Long levantadaPorId
) {}
