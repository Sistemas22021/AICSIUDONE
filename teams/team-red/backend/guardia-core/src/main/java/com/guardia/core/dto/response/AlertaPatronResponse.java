package com.guardia.core.dto.response;

import com.guardia.core.model.enums.EstadoAlerta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para una alerta interna de patrón de MO
 */
public record AlertaPatronResponse(
        Long id,
        Long expedienteOrigenId,
        String expedienteOrigenFolio,
        Long propuestaOrigenId,
        List<ExpedienteSimilarResponse> expedientesRelacionados,
        String resumenPatron,
        Double nivelConfianza,
        EstadoAlerta estado,
        LocalDateTime fechaGeneracion,
        UUID atendidaPorId,
        String atendidaPorNombre,
        LocalDateTime fechaAtencion,
        String motivoDescarte
) {}