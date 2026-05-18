package com.guardia.core.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record EscenaResponse(
        Long id,
        String estadoChecklist,
        LocalDateTime inicioProceso,
        LocalDateTime cierreProceso,
        Long expedienteId,
        UsuarioResponse levantadaPor,
        List<EvidenciaResponse> evidencias,
        List<EscenaNegativaResponse> escenasNegativas
) {}
