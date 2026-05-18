package com.guardia.core.dto.response;

import java.util.List;

public record ModusOperandiResponse(
        Long id,
        String descripcionAnalitica,
        String patronDetectado,
        String nivelConfianza,
        List<Long> expedienteIds
) {}
