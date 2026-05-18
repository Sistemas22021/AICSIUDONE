package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvidenciaRequest(
        String numeroItem,

        @NotBlank(message = "El tipo de evidencia es obligatorio")
        String tipo,

        String descripcion,

        @NotNull(message = "La escena es obligatoria")
        Long escenaId
) {}
