package com.guardia.core.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para una escena de levantamiento.
 * Contiene estado del checklist, paso actual, timestamps, evidencias y escenas negativas.
 */
public record EscenaResponse(
        Long id,
        String estadoChecklist,
        String estado,
        String pasoActual,
        LocalDateTime inicioProceso,
        LocalDateTime cierreProceso,
        Long expedienteId,
        UsuarioResponse levantadaPor,
        List<EvidenciaResponse> evidencias,
        List<EscenaNegativaResponse> escenasNegativas,
        UsuarioResponse liberadaPor,
        LocalDateTime horaLiberacion,
        String observacionesLiberacion,
        String hashLiberacion

) {}
