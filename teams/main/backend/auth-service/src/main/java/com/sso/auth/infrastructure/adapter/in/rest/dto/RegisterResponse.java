package com.sso.auth.infrastructure.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida para el registro exitoso.
 */
@Schema(description = "Respuesta del registro exitoso")
public record RegisterResponse(
        @Schema(description = "ID del usuario creado") String id,
        @Schema(description = "Username registrado") String username,
        @Schema(description = "Nombre completo") String fullName
) {}
