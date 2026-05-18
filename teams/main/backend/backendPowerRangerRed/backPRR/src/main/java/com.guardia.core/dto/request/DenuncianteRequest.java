package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DenuncianteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,

        String telefono,
        String nacionalidad,
        String direccion,
        String relacionConHecho
) {}
