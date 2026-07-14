package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para crear o modificar un subtipo de delito.
 * Contiene nombre, descripción y referencia al tipo padre.
 */
public record SubtipoDelitoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El tipo de delito es obligatorio")
        Long tipoDelitoId
) {}
