package com.sso.auth.infrastructure.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el login.
 */
@Schema(description = "Credenciales de autenticación")
public record LoginRequest(

        @NotBlank(message = "El username es obligatorio")
        @Schema(description = "Nombre de usuario", example = "john_doe")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña en texto plano", example = "SecurePass123!")
        String password
) {}
