package com.guardia.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler que valida existencia de header Authorization (Bearer ...).
 * También lee el header X-User-Role para simular rol del usuario.
 */
public class AuthenticationHandler extends AbstractRequestHandler {
    private static final String HEADER_AUTH = "Authorization";
    private static final String HEADER_ROLE = "X-User-Role";
    private static final String ATTR_USER_ROLE = "wife.middleware.user-role";

    @Override
    protected boolean doHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String auth = request.getHeader(HEADER_AUTH);
        if (auth == null || auth.isBlank() || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"Usuario no autenticado\"}");
            response.getWriter().flush();
            return false;
        }

        // For demo: read role from header and store as request attribute
        String role = request.getHeader(HEADER_ROLE);
        if (role != null && !role.isBlank()) {
            request.setAttribute(ATTR_USER_ROLE, role.trim());
        }
        return true;
    }
}
