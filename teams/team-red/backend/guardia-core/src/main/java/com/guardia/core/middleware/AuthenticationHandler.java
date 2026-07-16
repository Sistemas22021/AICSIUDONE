package com.guardia.core.middleware;

import com.guardia.core.security.SsoTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class AuthenticationHandler extends AbstractRequestHandler {
    private static final String HEADER_AUTH = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_ROLE = "X-User-Role";
    private static final String ATTR_USER_ROLE = "wife.middleware.user-role";

    public static final String ATTR_AUTHENTICATED_USERNAME = "guardia.middleware.authenticated-username";

    private final SsoTokenValidator ssoTokenValidator;

    public AuthenticationHandler(SsoTokenValidator ssoTokenValidator) {
        this.ssoTokenValidator = ssoTokenValidator;
    }

    @Override
    protected boolean doHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String auth = request.getHeader(HEADER_AUTH);
        if (auth == null || auth.isBlank() || !auth.startsWith(BEARER_PREFIX)) {
            return rechazar(response, "Debe iniciar sesión para acceder a este recurso.");
        }

        String token = auth.substring(BEARER_PREFIX.length()).trim();
        Optional<String> username = ssoTokenValidator.validarYExtraerUsuario(token);
        if (username.isEmpty()) {
            return rechazar(response, "La sesión no es válida o ha expirado. Inicie sesión nuevamente.");
        }

        request.setAttribute(ATTR_AUTHENTICATED_USERNAME, username.get());

        String role = request.getHeader(HEADER_ROLE);
        if (role != null && !role.isBlank()) {
            request.setAttribute(ATTR_USER_ROLE, role.trim());
        }
        return true;
    }

    private boolean rechazar(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + mensaje + "\"}");
        response.getWriter().flush();
        return false;
    }
}