package com.sso.auth.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida para el login exitoso.
 *
 * <p>Solo contiene el accessToken. El refreshToken se entrega
 * como HttpOnly Cookie (ver AuthController) y nunca aparece en este DTO.
 */
@Schema(description = "Respuesta del login exitoso")
@JsonInclude(JsonInclude.Include.NON_NULL) // No serializa campos null (ej: username en /refresh)
public record LoginResponse(
        @Schema(description = "JWT de acceso. Duración: 15 minutos. Guardar en memoria JS, NO en localStorage.")
        String accessToken,

        @Schema(description = "Username del usuario autenticado")
        String username
) {}
