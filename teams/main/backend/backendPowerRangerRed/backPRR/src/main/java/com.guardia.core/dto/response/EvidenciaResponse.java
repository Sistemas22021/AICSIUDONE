package com.guardia.core.dto.response;

public record EvidenciaResponse(
        Long id,
        String numeroItem,
        String tipo,
        String descripcion,
        Long escenaId
) {}
