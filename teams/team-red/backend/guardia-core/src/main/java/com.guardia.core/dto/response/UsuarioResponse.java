package com.guardia.core.dto.response;

/**
 * DTO de respuesta para representar usuarios en respuestas de la API.
 * Incluye id, nombre, identificación y correo.
 */
public record UsuarioResponse(
        Long id,
        String nombre,
        String identificacion,
        String correo
) {}
