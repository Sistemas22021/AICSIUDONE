package com.sso.auth.domain.model;

import java.time.Instant;

/**
 * Value Object de dominio: Token de refresco.
 *
 * <p>Representa el Refresh Token almacenado en la base de datos.
 * Su valor es un string opaco (UUID aleatorio), no un JWT.
 * Esto permite invalidarlo explícitamente desde el servidor
 * eliminándolo de la base de datos.
 *
 * <p><b>¿Por qué opaco y no JWT?</b><br>
 * Un JWT no puede invalidarse antes de su expiración sin una lista negra.
 * Un token opaco en la BD sí puede eliminarse en cualquier momento (logout).
 */
public record RefreshToken(
        String token,        // UUID aleatorio, guardado como string en la BD
        String username,     // A qué usuario pertenece
        Instant expiresAt    // Cuándo expira (7 días por defecto)
) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
