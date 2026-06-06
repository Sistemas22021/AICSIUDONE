package com.guardia.core.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record EscenaResponse(
        Long id,
        String estadoChecklist,
        String pasoActual,
        LocalDateTime inicioProceso,
        LocalDateTime cierreProceso,
        Long expedienteId,
        UsuarioResponse levantadaPor,
        List<EvidenciaResponse> evidencias,
        List<EscenaNegativaResponse> escenasNegativas

) {}
