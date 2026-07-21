package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para crear o actualizar un usuario del sistema.
 * Campos: nombre, identificación, credenciales y correo.
 */
public record UsuarioRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String fullName,

        @Size(max = 500, message = "La URL de la foto no puede superar los 500 caracteres")
        String profilePhotoUrl
) { }
