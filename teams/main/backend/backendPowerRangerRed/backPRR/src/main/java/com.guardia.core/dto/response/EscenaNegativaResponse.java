package com.guardia.core.dto.response;

public record EscenaNegativaResponse(
        Long id,
        String elementoBuscado,
        String areaInspeccionada,
        String resultado,
        String observacion,
        Long escenaId
) {}
