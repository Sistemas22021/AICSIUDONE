package com.guardia.core.dto.response;

public record SubtipoDelitoResponse(
        Long id,
        String nombre,
        String descripcion,
        Long tipoDelitoId,
        String tipoDelitoNombre
) {}
