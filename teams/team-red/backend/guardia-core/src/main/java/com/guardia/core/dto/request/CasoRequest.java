package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CasoRequest(

        @NotNull(message = "Debe indicar el usuario que realiza la agrupación.")
        String creadoPorUsername,

        @NotNull(message = "Debe indicar al menos dos expedientes.")
        @Size(min = 2, message = "Un caso debe agrupar al menos dos expedientes.")
        List<Long> expedienteIds,

        @NotBlank(message = "El motivo de la agrupación es obligatorio.")
        String motivo,

        UUID alertaOrigenId
) {
}