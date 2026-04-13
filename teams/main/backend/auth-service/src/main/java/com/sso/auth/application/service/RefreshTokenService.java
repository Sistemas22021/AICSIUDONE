package com.sso.auth.application.service;

import com.sso.auth.domain.model.RefreshToken;
import com.sso.auth.domain.port.in.RefreshTokenUseCase;
import com.sso.auth.domain.port.out.RefreshTokenRepository;
import com.sso.auth.infrastructure.config.JwtTokenProvider;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Refresco de token.
 *
 * <p>Valida el Refresh Token (desde la BD), verifica que no haya expirado,
 * y emite un nuevo Access Token JWT.
 *
 * <p><b>Nota:</b> Esta implementación usa Refresh Token Simple (no rotación).
 * El mismo refresh token es válido hasta su fecha de expiración.
 * Ver ADR-002 para la justificación y la ruta de migración a rotación.
 */
@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String refresh(String token) {
        // 1. Buscar el token en la BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        // 2. Verificar que no haya expirado
        if (refreshToken.isExpired()) {
            // Limpiar token expirado de la BD
            refreshTokenRepository.deleteByUsername(refreshToken.username());
            throw new InvalidRefreshTokenException();
        }

        // 3. Emitir nuevo Access Token JWT
        return jwtTokenProvider.generateAccessToken(refreshToken.username());
    }
}
