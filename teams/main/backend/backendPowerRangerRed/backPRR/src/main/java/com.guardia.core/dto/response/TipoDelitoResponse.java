package com.guardia.core.dto.response;

import java.util.List;

public record TipoDelitoResponse(
        Long id,
        String nombre,
        String descripcion,
        Boolean requiereSubtipo,
        List<SubtipoDelitoResponse> subtipos
) {}
