package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,

        @NotBlank(message = "Las credenciales son obligatorias")
        String credenciales,

        @NotBlank(message = "El correo es obligatorio")
        String correo
) {}
