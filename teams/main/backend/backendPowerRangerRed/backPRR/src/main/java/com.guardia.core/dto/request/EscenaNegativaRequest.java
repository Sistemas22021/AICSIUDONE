package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EscenaNegativaRequest(
        @NotBlank(message = "El elemento buscado es obligatorio")
        String elementoBuscado,

        @NotBlank(message = "El área inspeccionada es obligatoria")
        String areaInspeccionada,

        String resultado,
        String observacion,

        @NotNull(message = "La escena es obligatoria")
        Long escenaId
) {}
