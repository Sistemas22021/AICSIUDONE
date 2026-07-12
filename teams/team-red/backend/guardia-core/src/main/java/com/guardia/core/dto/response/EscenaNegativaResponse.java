package com.guardia.core.dto.response;

/**
 * DTO de respuesta para elementos de una escena negativa (sin hallazgos).
 */
public record EscenaNegativaResponse(
        Long id,
        String elementoBuscado,
        String areaInspeccionada,
        String resultado,
        String observacion,
        Long escenaId,
        Boolean sinElementosNegativos
) {}
