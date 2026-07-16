package com.guardia.core.dto.response;

import com.guardia.core.model.enums.EstadoPropuestaMO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta con el contenido de la propuesta de MO generada por IA
 * (HU2) y su trazabilidad de validación experta (HU3).
 */
public record PropuestaModusOperandiResponse(
        Long id,
        Long expedienteId,
        String folioExpediente,
        Integer version,
        boolean vigente,
        EstadoPropuestaMO estado,
        String caracteristicasComunes,
        String posibleFirma,
        String consistenciaHorarioZona,
        String resumenGenerado,
        Double nivelConfianza,
        String modeloEmbedding,
        String modeloChat,
        LocalDateTime fechaGeneracion,
        List<ExpedienteSimilarResponse> expedientesSimilares,
        boolean revisadoPorExperto,
        Long analistaRevisorId,
        String analistaRevisorNombre,
        String justificacionRevision,
        String clasificacionManual,
        LocalDateTime fechaRevision
) {}