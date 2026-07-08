package com.guardia.core.dto.response;

import java.time.LocalDateTime;

/**
 * DTO plano para el panel de expedientes del frontend.
 * "estatus" expone el nombre real del EstadoExpediente del backend.
 * investigadorAsignado y tieneAlertaPatron son placeholders hasta que
 * esos conceptos existan en el modelo de datos.
 */
public record ExpedienteActivoResponse(
        String id,
        String folioCOPP,
        String tipoDelito,
        String subtipoDelito,
        LocalDateTime fechaHecho,
        LocalDateTime fechaCreacion,
        String investigadorAsignado,
        String estatus,
        boolean tieneAlertaPatron,
        String municipio,
        String sector
) {}