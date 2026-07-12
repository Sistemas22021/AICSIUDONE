package com.guardia.core.dto.response;

import java.util.List;

/**
 * DTO de respuesta que devuelve un tipo de delito con sus subtipos.
 */
public record TipoDelitoResponse(
        Long id,
        String nombre,
        String descripcion,
        Boolean requiereSubtipo,
        List<SubtipoDelitoResponse> subtipos
) {}
