package com.guardia.core.dto.response;

/**
 * DTO de respuesta para subtipo de delito, incluye referencia al tipo padre.
 */
public record SubtipoDelitoResponse(
        Long id,
        String nombre,
        String descripcion,
        Long tipoDelitoId,
        String tipoDelitoNombre
) {}
