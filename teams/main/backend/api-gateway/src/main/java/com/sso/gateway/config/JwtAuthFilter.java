package com.sso.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Filtro JWT del API Gateway.
 *
 * <p>Intercepta todas las peticiones a rutas protegidas.
 * Si el token es válido, extrae el username y lo pasa como header
 * al microservicio destino (para que sepa quién es el usuario sin
 * necesitar validar el token de nuevo).
 *
 * <p><b>Flujo:</b>
 * <ol>
 *   <li>Extraer el header {@code Authorization: Bearer <token>}</li>
 *   <li>Validar firma y expiración del JWT</li>
 *   <li>Si válido: agregar header {@code X-User-Name} y delegar</li>
 *   <li>Si inválido: retornar 401 Unauthorized</li>
 * </ol>
 *
 * <p><b>Nota WebFlux:</b> Al ser reactivo, se usa {@code Mono<Void>}
 * en lugar de {@code void}. GatewayFilter es diferente a Servlet Filter.
 */
@Component
public class JwtAuthFilter implements GatewayFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String X_USER_NAME_HEADER = "X-User-Name";

    private final SecretKey signingKey;

    public JwtAuthFilter(@Value("${sso.auth.jwt-secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 1. Verificar que existe el header Authorization
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // 2. Validar el JWT
        String username = extractUsername(token);
        if (username == null) {
            return unauthorized(exchange);
        }

        // 3. Propagar el username al microservicio destino como header
        var mutatedRequest = exchange.getRequest().mutate()
                .header(X_USER_NAME_HEADER, username)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null; // Token inválido o expirado
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
