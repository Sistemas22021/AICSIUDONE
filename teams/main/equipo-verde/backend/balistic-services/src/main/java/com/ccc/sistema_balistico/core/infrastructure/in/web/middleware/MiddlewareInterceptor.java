package com.ccc.sistema_balistico.core.infrastructure.in.web.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Interceptor Web de Spring MVC que engancha las peticiones entrantes
 * y activa la Cadena de Responsabilidad (AuthenticationHandler -> RoleValidationHandler).
 */
@Component
public class MiddlewareInterceptor implements HandlerInterceptor {

    private final AuthenticationHandler authenticationHandler;

    public MiddlewareInterceptor(AuthenticationHandler authenticationHandler, RoleValidationHandler roleValidationHandler) {
        this.authenticationHandler = authenticationHandler;
        // Ensamblar la Cadena de Responsabilidad: Eslabón 1 (Auth) -> Eslabón 2 (Rol)
        this.authenticationHandler.setNext(roleValidationHandler);
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String uri = request.getRequestURI();

        // Permitir solicitudes de pre-vuelo CORS (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        // Excluir rutas de infraestructura, salud y documentación Swagger / OpenAPI
        if (uri.contains("/swagger")
                || uri.contains("/api-docs")
                || uri.contains("/error")
                || uri.contains("/actuator")) {
            return true;
        }

        // Iniciar la Cadena de Responsabilidad llamando al primer eslabón
        return authenticationHandler.handle(request, response, handler);
    }
}
