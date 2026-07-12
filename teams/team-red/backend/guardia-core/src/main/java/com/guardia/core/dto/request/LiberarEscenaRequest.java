package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para liberar formalmente una escena del crimen.
 * Contiene el investigador responsable del cierre y observaciones opcionales.
 */
public record LiberarEscenaRequest(
        @NotNull(message = "El investigador responsable de la liberación es obligatorio")
        Long investigadorResponsableId,

        @Size(max = 2000, message = "Las observaciones no pueden superar los 2000 caracteres")
        String observaciones
) {}