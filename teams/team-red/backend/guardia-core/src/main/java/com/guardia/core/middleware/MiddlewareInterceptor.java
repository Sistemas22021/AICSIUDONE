package com.guardia.core.middleware;

import com.guardia.core.security.SsoTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class MiddlewareInterceptor implements HandlerInterceptor {

    private final SsoTokenValidator ssoTokenValidator;
    private final RoleValidationHandler roleValidationHandler;

    public MiddlewareInterceptor(SsoTokenValidator ssoTokenValidator, RoleValidationHandler roleValidationHandler) {
        this.ssoTokenValidator = ssoTokenValidator;
        this.roleValidationHandler = roleValidationHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String uri = request.getRequestURI();

        if (uri.equals("/swagger")
                || uri.startsWith("/swagger/")
                || uri.startsWith("/swagger-ui/")
                || uri.equals("/swagger-ui.html")
                || uri.startsWith("/api-docs")
                || uri.startsWith("/v3/api-docs")) {//
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        // 1. Validar autenticación (JWT)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return rechazar(response, "Debe iniciar sesión para acceder a este recurso.");
        }

        String token = authHeader.substring(7);
        var usernameOpt = ssoTokenValidator.validarYExtraerUsuario(token);
        if (usernameOpt.isEmpty()) {
            return rechazar(response, "La sesión no es válida o ha expirado. Inicie sesión nuevamente.");
        }

        // Guardar username en el request para uso posterior
        request.setAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME, usernameOpt.get());

        // 2. Validar roles
        return roleValidationHandler.preHandle(request, response, handler);
    }

    private boolean rechazar(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + mensaje + "\"}");
        response.getWriter().flush();
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
    }
}