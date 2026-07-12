package com.guardia.core.dto.response;

import java.util.List;

/**
 * DTO de respuesta para modus operandi, lista expedientes relacionados y patrón detectado.
 */
public record ModusOperandiResponse(
        Long id,
        String descripcionAnalitica,
        String patronDetectado,
        String nivelConfianza,
        List<Long> expedienteIds
) {}
