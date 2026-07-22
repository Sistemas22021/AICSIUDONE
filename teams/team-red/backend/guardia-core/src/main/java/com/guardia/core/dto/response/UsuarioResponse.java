package com.guardia.core.dto.response;

import java.util.UUID;

/**
 * DTO de respuesta para representar usuarios en respuestas de la API.
 * Incluye id, nombre, identificación y correo.
 */
public record UsuarioResponse(
        UUID id,
        String username,
        String fullName,
        String profilePhotoUrl,
        String rol
) {}
