package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ModusOperandiRequest(
        @NotBlank(message = "La descripción analítica es obligatoria")
        String descripcionAnalitica,

        String patronDetectado,
        String nivelConfianza
) {}
