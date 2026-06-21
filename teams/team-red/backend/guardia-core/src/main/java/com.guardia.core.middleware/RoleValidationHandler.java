package com.guardia.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Valida roles según endpoint y operación.
 * Roles esperados en header X-User-Role (poblado por AuthenticationHandler).
 */
public class RoleValidationHandler extends AbstractRequestHandler {
    private static final String ATTR_USER_ROLE = "wife.middleware.user-role";

    private static final Set<String> ROLE_CREAR_EXPEDIENTE = Set.of("INVESTIGADOR", "ADMIN");
    private static final Set<String> ROLE_SELLAR = Set.of("SUPERVISOR", "ADMIN");
    private static final Set<String> ROLE_CREAR_EVIDENCIA = Set.of("INVESTIGADOR", "ADMIN");

    @Override
    protected boolean doHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        String role = (String) request.getAttribute(ATTR_USER_ROLE);
        if (role == null) {
            // if no role provided, deny
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"No posee permisos para ejecutar esta operación\"}");
            response.getWriter().flush();
            return false;
        }

        // Check specific rules
        // Crear expediente: POST /api/expedientes
        if ("POST".equals(method) && uri.equals("/api/expedientes")) {
            if (!ROLE_CREAR_EXPEDIENTE.contains(role.toUpperCase())) {
                deny(response);
                return false;
            }
        }

        // Sellar expediente: PUT /api/expedientes/{id}/sellar
        if ("PUT".equals(method) && uri.matches("^/api/expedientes/\\d+/sellar$")) {
            if (!ROLE_SELLAR.contains(role.toUpperCase())) {
                deny(response);
                return false;
            }
        }

        // Crear evidencia: POST /api/evidencias
        if ("POST".equals(method) && uri.equals("/api/evidencias")) {
            if (!ROLE_CREAR_EVIDENCIA.contains(role.toUpperCase())) {
                deny(response);
                return false;
            }
        }

        return true;
    }

    private void deny(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"No posee permisos para ejecutar esta operación\"}");
        response.getWriter().flush();
    }
}
