package com.guardia.core.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record FirmaConductualResponse(
        Long id,
        Long expedienteId,
        String folioExpediente,
        Integer version,
        boolean vigente,
        String comportamientoPreDelictivo,
        String metodoAproximacion,
        String metodoAtaque,
        String comportamientoPostDelictivo,
        String elementosDistintivos,
        UUID analistaId,
        String analistaNombre,
        LocalDateTime fechaRegistro
) {}
