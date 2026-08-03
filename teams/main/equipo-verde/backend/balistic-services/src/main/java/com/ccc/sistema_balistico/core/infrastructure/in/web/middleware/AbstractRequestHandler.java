package com.ccc.sistema_balistico.core.infrastructure.in.web.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Clase base abstracta del patrón Cadena de Responsabilidad.
 * Gestiona la propagación secuencial entre eslabones (autenticación -> autorización).
 */
public abstract class AbstractRequestHandler implements RequestHandler {
    protected RequestHandler next;

    @Override
    public void setNext(RequestHandler next) {
        this.next = next;
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!doHandle(request, response, handler)) {
            return false;
        }
        if (next != null) {
            return next.handle(request, response, handler);
        }
        return true;
    }

    protected abstract boolean doHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException;

    protected boolean reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\", \"status\": " + status + "}");
        response.getWriter().flush();
        return false;
    }
}
