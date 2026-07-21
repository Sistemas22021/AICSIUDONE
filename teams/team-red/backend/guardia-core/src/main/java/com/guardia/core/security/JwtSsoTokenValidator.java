package com.guardia.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Adaptador de {@link SsoTokenValidator} que valida los JWT emitidos por
 * {@code main/backend/auth-service}.
 *
 * <p><b>Integración sin tocar {@code main/}:</b><br>
 * guardia-core no llama por red al auth-service para validar cada
 * petición (no hay endpoint de introspección expuesto). En su lugar,
 * valida el JWT de forma local usando exactamente el mismo esquema que ya
 * usan {@code auth-service} y {@code api-gateway} dentro de {@code main/}:
 * <ul>
 *   <li>Algoritmo: HMAC-SHA256, firmado con la clave
 *       {@code sso.auth.jwt-secret} (ver
 *       {@code com.sso.auth.infrastructure.config.JwtTokenProvider} y
 *       {@code com.sso.gateway.config.JwtAuthFilter} en {@code main/}).</li>
 *   <li>Claim {@code sub}: contiene el username del usuario autenticado.</li>
 * </ul>
 * Como la clave (`sso.auth.jwt-secret`) se configura por variable de
 * entorno ({@code JWT_SECRET}) con el mismo valor por defecto que usa
 * {@code main/}, guardia-core puede verificar la firma sin ninguna
 * llamada adicional ni ningún cambio dentro de {@code main/}: basta con
 * que ambos servicios reciban el mismo secreto en su entorno de
 * ejecución.
 *
 * <p>Si en el futuro se agrega un endpoint de introspección o
 * verificación remota en el auth-service, este es el único punto que
 * habría que reemplazar (Open/Closed Principle vía {@link SsoTokenValidator}).
 */
@Component
public class JwtSsoTokenValidator implements SsoTokenValidator {

    private final SecretKey signingKey;

    public JwtSsoTokenValidator(@Value("${sso.auth.jwt-secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Optional<String> validarYExtraerUsuario(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            return (username == null || username.isBlank())
                    ? Optional.empty()
                    : Optional.of(username);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}