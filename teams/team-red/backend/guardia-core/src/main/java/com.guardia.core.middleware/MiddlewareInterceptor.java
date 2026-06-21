package com.guardia.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Interceptor que ejecuta la cadena de responsabilidad para endpoints específicos.
 */
@Component
public class MiddlewareInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Endpoints where middleware should run
        boolean shouldApply = ("POST".equals(method) && uri.equals("/api/expedientes"))
                || ("PUT".equals(method) && uri.matches("^/api/expedientes/\\d+/sellar$"))
                || ("POST".equals(method) && uri.equals("/api/evidencias"));

        if (!shouldApply) return true;

        // Build chain per-request to avoid shared mutable state
        AuthenticationHandler auth = new AuthenticationHandler();
        RequestLoggingHandler loggerHandler = new RequestLoggingHandler();
        RoleValidationHandler roleValidator = new RoleValidationHandler();

        auth.setNext(loggerHandler);
        loggerHandler.setNext(roleValidator);

        auth.handle(request, response);

        // If response has been committed or status indicates error, stop processing
        if (response.isCommitted() || response.getStatus() >= 400) {
            return false;
        }

        return true; // continue to controller
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        // no-op
    }
}
