package com.sso.auth.domain.port.out;

import com.sso.auth.domain.model.RefreshToken;

import java.util.Optional;

/**
 * Puerto de salida (Repository): Refresh Tokens.
 *
 * <p>Gestiona la persistencia de los tokens de refresco.
 * Los tokens se almacenan en la BD para poder invalidarlos
 * explícitamente en el logout.
 */
public interface RefreshTokenRepository {

    /** Persiste un nuevo refresh token */
    RefreshToken save(RefreshToken refreshToken);

    /** Busca un refresh token por su valor opaco */
    Optional<RefreshToken> findByToken(String token);

    /** Elimina el refresh token del usuario (logout) */
    void deleteByUsername(String username);
}
