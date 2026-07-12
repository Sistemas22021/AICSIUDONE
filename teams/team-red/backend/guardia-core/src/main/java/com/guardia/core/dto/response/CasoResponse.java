package com.guardia.core.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CasoResponse(
        Long id,
        String codigoCaso,
        String motivo,
        UsuarioResponse creadoPor,
        LocalDateTime fechaCreacion,
        Long alertaOrigenId,
        List<ExpedienteResumenResponse> expedientes
) {
}