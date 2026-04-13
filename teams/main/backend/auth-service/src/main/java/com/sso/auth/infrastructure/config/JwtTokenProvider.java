package com.sso.auth.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Componente de infraestructura: Generación y validación de JWT.
 *
 * <p>Encapsula toda la lógica de JWT en un único lugar para que
 * los servicios de aplicación no dependan directamente de la librería JJWT.
 *
 * <p><b>¿Por qué está en infraestructura y no en dominio?</b><br>
 * JWT es un detalle de implementación (un formato de serialización de claims).
 * El dominio solo conoce "un token de acceso". Cómo se construye es responsabilidad
 * de la infraestructura.
 *
 * <p><b>Configuración (desde config-repo/auth-service-{profile}.yml):</b>
 * <ul>
 *   <li>{@code sso.auth.jwt-secret}: clave secreta HS256 (mínimo 256 bits)</li>
 *   <li>{@code sso.auth.access-token-expiry-minutes}: duración del access token</li>
 * </ul>
 */
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long accessTokenExpiryMs;

    public JwtTokenProvider(
            @Value("${sso.auth.jwt-secret}") String jwtSecret,
            @Value("${sso.auth.access-token-expiry-minutes:15}") int expiryMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = (long) expiryMinutes * 60 * 1000;
    }

    /**
     * Genera un JWT firmado con HS256.
     *
     * @param username sujeto del token
     * @return JWT como String compacto (header.payload.signature)
     */
    public String generateAccessToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiryMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extrae el username del JWT. Lanza JwtException si el token es inválido.
     *
     * @param token JWT a validar
     * @return username extraído del claim 'sub'
     */
    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Valida la firma y expiración del JWT.
     *
     * @param token JWT a validar
     * @return true si el token es válido y no ha expirado
     */
    public boolean isValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
