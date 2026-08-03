package com.ccc.sistema_balistico.core.infrastructure.in.web.middleware;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Eslabón 1 de la Cadena de Responsabilidad:
 * Verifica si el usuario está autenticado, evaluando primero el header X-User-Name (procedente del API Gateway)
 * o validando criptográficamente el token JWT Bearer (procedente de consulta directa o cliente frontend).
 */
@Component
public class AuthenticationHandler extends AbstractRequestHandler {

    public static final String ATTR_AUTHENTICATED_USERNAME = "ev.balistics.authenticated-username";
    private static final String X_USER_NAME_HEADER = "X-User-Name";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey signingKey;
    private final boolean authEnabled;

    public AuthenticationHandler(
            @Value("${sso.auth.jwt-secret:este-es-un-secreto-de-desarrollo-cambiar-en-produccion-32chars}") String jwtSecret,
            @Value("${sso.auth.enabled:true}") boolean authEnabled) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.authEnabled = authEnabled;
    }

    @Override
    protected boolean doHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // En entorno de tests automáticos (sso.auth.enabled=false), inyectar usuario perito de prueba
        if (!authEnabled) {
            request.setAttribute(ATTR_AUTHENTICATED_USERNAME, "perito-test");
            return true;
        }

        // 1. Verificar si ya viene pre-autenticado por el API Gateway en la cabecera X-User-Name
        String gatewayUsername = request.getHeader(X_USER_NAME_HEADER);
        if (gatewayUsername != null && !gatewayUsername.isBlank()) {
            request.setAttribute(ATTR_AUTHENTICATED_USERNAME, gatewayUsername.trim());
            return true;
        }

        // 2. Si no es reenvío del Gateway, inspeccionar el token JWT en el encabezado Authorization: Bearer <jwt>
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "Acceso no autorizado. Debe iniciar sesión (Falta token de sesión).");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o sin sujeto identificado.");
            }
            request.setAttribute(ATTR_AUTHENTICATED_USERNAME, username);
            return true;
        } catch (Exception e) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "La sesión no es válida o ha expirado (" + e.getMessage() + ").");
        }
    }
}
