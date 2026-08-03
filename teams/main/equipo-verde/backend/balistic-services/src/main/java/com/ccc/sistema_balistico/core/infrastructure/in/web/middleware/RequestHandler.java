package com.ccc.sistema_balistico.core.infrastructure.in.web.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interfaz del patrón Cadena de Responsabilidad (Chain of Responsibility)
 * para la autenticación y validación de roles en las peticiones HTTP.
 */
public interface RequestHandler {
    void setNext(RequestHandler next);
    boolean handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException;
}
