package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Payload para registrar o editar (nueva versión) la firma conductual de un
 * expediente. Los 5 campos de contenido son individualmente opcionales; la
 * regla "al menos uno obligatorio" se valida en FirmaConductualServiceImpl
 * porque Bean Validation no expresa bien reglas "al menos uno de N".
 */
public record FirmaConductualRequest(
        @NotNull(message = "Debe indicar el analista que registra la firma conductual.")
        UUID analistaId,

        String comportamientoPreDelictivo,
        String metodoAproximacion,
        String metodoAtaque,
        String comportamientoPostDelictivo,
        String elementosDistintivos
) {}
