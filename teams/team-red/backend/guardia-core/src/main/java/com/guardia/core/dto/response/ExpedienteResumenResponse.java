package com.guardia.core.dto.response;

import com.guardia.core.model.enums.EstadoExpediente;

import java.time.LocalDateTime;

public record ExpedienteResumenResponse(
        Long id,
        String folio,
        String tipoDelito,
        String subtipoDelito,
        EstadoExpediente estatus,
        LocalDateTime fechaHecho,
        String investigadorAsignado,
        String municipio
) {
}