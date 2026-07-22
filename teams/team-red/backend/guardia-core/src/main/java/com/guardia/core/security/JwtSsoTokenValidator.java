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