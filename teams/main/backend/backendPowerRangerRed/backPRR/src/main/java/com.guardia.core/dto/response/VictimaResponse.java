package com.guardia.core.dto.response;

/**
 * DTO de respuesta que representa una víctima registrada.
 * Incluye id, nombre, identificación, contacto y referencia al expediente.
 */
public record VictimaResponse(
        Long id,
        String nombre,
        String identificacion,
        String telefono,
        String nacionalidad,
        String direccion,
        Long expedienteId
) {}
