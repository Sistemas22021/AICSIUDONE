package com.guardia.core.dto.response;

import com.guardia.core.model.enums.EstadoExpediente;

import java.time.LocalDateTime;
import java.util.List;

public record ExpedienteResponse(
        Long id,
        String folio,
        EstadoExpediente estadoExpediente,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaSellado,
        String descripcionHecho,
        LocalDateTime fechaHecho,
        UsuarioResponse creadoPor,
        UsuarioResponse selladoPor,
        TipoDelitoResponse tipoDelito,
        SubtipoDelitoResponse subtipoDelito,
        LocalizacionResponse localizacion,
        List<EscenaResponse> escenas,
        List<InvolucradoResponse> involucrados

) {}
