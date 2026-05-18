package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VictimaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,

        String telefono,
        String nacionalidad,
        String direccion,

        @NotNull(message = "El expediente es obligatorio")
        Long expedienteId
) {}
