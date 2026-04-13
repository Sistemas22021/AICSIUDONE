package com.sso.auth.infrastructure.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para el registro de usuario.
 * Las anotaciones @NotBlank y @Size evitan que lleguen datos inválidos al caso de uso.
 */
@Schema(description = "Datos requeridos para registrar un nuevo usuario")
public record RegisterRequest(

        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        @Schema(description = "Nombre de usuario único", example = "john_doe")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Schema(description = "Contraseña en texto plano (se hashea con BCrypt)", example = "SecurePass123!")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Schema(description = "Nombre completo del usuario", example = "John Doe")
        String fullName,

        @Schema(description = "URL de la foto de perfil (opcional)", example = "https://example.com/photo.jpg")
        String profilePhotoUrl
) {}
