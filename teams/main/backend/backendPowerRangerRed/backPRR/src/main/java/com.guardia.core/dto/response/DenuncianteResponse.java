package com.guardia.core.dto.response;

/**
 * DTO de respuesta para un denunciante registrado.
 */
public record DenuncianteResponse(
        Long id,
        String nombre,
        String identificacion,
        String telefono,
        String nacionalidad,
        String direccion,
        String relacionConHecho
) {}
