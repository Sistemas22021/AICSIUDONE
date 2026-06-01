package com.sso.auth.domain.port.in;

/**
 * Puerto de entrada (Use Case): Refresco de token.
 *
 * <p>Valida el Refresh Token enviado (desde HttpOnly Cookie),
 * lo invalida, y emite un nuevo Access Token JWT.
 *
 * <p><b>Estrategia: Refresh Token Simple</b><br>
 * El mismo Refresh Token es reutilizable hasta su expiración (7 días).
 * Para una implementación futura con rotación, se reemplazaría por
 * uno nuevo en cada uso (ver ADR-002).
 */
public interface RefreshTokenUseCase {

    /**
     * Emite un nuevo Access Token usando el Refresh Token.
     *
     * @param refreshToken el token opaco enviado desde el HttpOnly Cookie
     * @return Nuevo Access Token JWT
     * @throws InvalidRefreshTokenException si el token es inválido o expiró
     */
    String refresh(String refreshToken);

    /** Excepción de dominio: el refresh token es inválido o expiró */
    class InvalidRefreshTokenException extends RuntimeException {
        public InvalidRefreshTokenException() {
            super("Refresh token inválido o expirado. Por favor, inicia sesión nuevamente.");
        }
    }
}
