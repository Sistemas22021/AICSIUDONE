package com.nexocriminal.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Conecta la Cadena de Responsabilidad con el pipeline de Spring.
 *
 * Las rutas publicas (login, estado) se saltan la cadena.
 */
@Component
@RequiredArgsConstructor
public class MiddlewareFilter extends OncePerRequestFilter {

    private final CadenaMiddleware cadena;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Rutas que NO pasan por la cadena (publicas)
    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/registrar",
            "/files/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String metodo = request.getMethod();

        // Excepción para el equipo Blanco: el GET de personas es público (solo lectura)
        boolean esConsultaPersonasPublica =
                path.startsWith("/api/v1/personas") && "GET".equalsIgnoreCase(metodo);

        // Las rutas públicas se saltan la cadena
        boolean esPublica = RUTAS_PUBLICAS.stream().anyMatch(path::startsWith);
        if (esPublica || esConsultaPersonasPublica || !path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ejecutar la Cadena de Responsabilidad
        ContextoPeticion contexto = new ContextoPeticion(request);
        cadena.ejecutar(contexto);

        if (contexto.isRechazado()) {
            response.setStatus(contexto.getCodigoError());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Map.of("error", contexto.getMensajeError())
            ));
            return; // corta aquí, no llega al controller
        }

        // La cadena dejó pasar: continuar al controller
        filterChain.doFilter(request, response);
    }
}