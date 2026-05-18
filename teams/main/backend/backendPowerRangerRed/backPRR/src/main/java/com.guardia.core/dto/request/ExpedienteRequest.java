package com.guardia.core.dto.request;

import com.guardia.core.model.enums.EstadoExpediente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ExpedienteRequest(
        @NotBlank(message = "La descripción del hecho es obligatoria")
        String descripcionHecho,

        @NotNull(message = "La fecha del hecho es obligatoria")
        LocalDateTime fechaHecho,

        @NotNull(message = "El tipo de delito es obligatorio")
        Long tipoDelitoId,

        Long subtipoDelitoId,

        @NotNull(message = "La localización es obligatoria")
        Long localizacionId,

        Long denuncianteId,

        @NotNull(message = "El usuario creador es obligatorio")
        Long creadoPorId
) {}
