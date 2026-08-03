package com.guardia.core.middleware;

import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleValidationHandler implements HandlerInterceptor {

    private final UsuarioRepository usuarioRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Obtener username del request
        String username = (String) request.getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME);
        if (username == null) {
            return rechazar(response, "Usuario no autenticado");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return rechazar(response, "Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        String rol = usuario.getRol();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Verificar permisos según rol
        boolean permitido = verificarPermisos(rol, method, uri);

        if (!permitido) {
            String mensaje = generarMensajeError(rol);
            return rechazar(response, mensaje);
        }

        request.setAttribute("X-User-Role", rol);
        return true;
    }

    private boolean verificarPermisos(String rol, String method, String uri) {
        // ─── OFICIAL: Solo expedientes ──────────────────────────────────────
        if ("OFICIAL".equalsIgnoreCase(rol)) {
            // Puede ver expedientes (GET)
            if (method.equals("GET") && uri.startsWith("/api/v1/expedientes")) {
                return true;
            }

            // Puede registrar expediente (POST)
            if (method.equals("POST") && uri.equals("/api/v1/expedientes")) {
                return true;
            }

            // Puede registrar expediente vía el adaptador de incidentes (POST)
            if (method.equals("POST") && uri.equals("/api/v1/incidentes")) {
                return true;
            }

            // Puede sellar expediente (PATCH)
            if (method.equals("PATCH") && uri.matches("^/api/v1/expedientes/\\d+/sellar$")) {
                return true;
            }

            // Puede ver casos (GET)
            if (method.equals("GET") && uri.startsWith("/api/v1/casos")) {
                return true;
            }

            // Puede ver delitos y categorías (GET)
            if (method.equals("GET") && uri.startsWith("/api/v1/delitos")) {
                return true;
            }

            // Puede ver tipos y subtipos de delito (GET)
            if (method.equals("GET") && (
                    uri.startsWith("/api/v1/tipos-delito") ||
                            uri.startsWith("/api/v1/subtipos-delito")
            )) {
                return true;
            }

            // Puede ver usuarios (GET)
            if (method.equals("GET") && uri.startsWith("/api/v1/usuarios")) {
                return true;
            }

            // Puede ver localizaciones (GET)
            if (method.equals("GET") && uri.startsWith("/api/v1/localizaciones")) {
                return true;
            }

            // Puede ver involucrados (GET)
            if (method.equals("GET") && uri.startsWith("/api/v1/involucrados")) {
                return true;
            }

            // Panel del Guardia y acciones sobre alertas de patrón de MO (HU6, CA3):
            // ver el panel/bandeja (GET) y marcar una alerta revisada/descartada (PATCH).
            return uri.startsWith("/api/v1/alertas") && (method.equals("GET") || method.equals("PATCH"));
        }

        // ─── ANALISTA: Escenas, Evidencias y MO ────────────────────────────
        if ("ANALISTA".equalsIgnoreCase(rol)) {
            // Escenas - CRUD completo
            if (uri.startsWith("/api/v1/escenas")) {
                return true;
            }

            // Escenas Negativas - CRUD completo
            if (uri.startsWith("/api/v1/escenas-negativas")) {
                return true;
            }

            // Evidencias - CRUD completo
            if (uri.startsWith("/api/v1/evidencias")) {
                return true;
            }

            // Modus Operandi - CRUD completo
            if (uri.contains("/modus-operandi") || uri.startsWith("/api/v1/modus-operandi")) {
                return true;
            }

            // Firma conductual - CRUD completo
            if (uri.contains("/firma-conductual") || uri.startsWith("/api/v1/firmas-conductuales")) {
                return true;
            }

            // Puede forzar el reanálisis de Modus Operandi de un expediente (POST)
            if (method.equals("POST") && uri.matches("^/api/v1/expedientes/\\d+/reanalizar-mo$")) {
                return true;
            }

            // Propuestas MO - CRUD completo
            if (uri.startsWith("/api/v1/propuestas-mo")) {
                return true;
            }

            // Panel del Guardia / bandeja del Investigador y acciones sobre alertas
            // de patrón de MO (HU6, CA3): ver (GET) y marcar revisada/descartada (PATCH).
            if (uri.startsWith("/api/v1/alertas")) {
                return method.equals("GET") || method.equals("PATCH");
            }

            // Ver expedientes (solo lectura)
            if (method.equals("GET") && uri.startsWith("/api/v1/expedientes")) {
                return true;
            }

            // Ver casos (solo lectura)
            if (method.equals("GET") && uri.startsWith("/api/v1/casos")) {
                return true;
            }

            // Ver delitos y categorías (solo lectura)
            if (method.equals("GET") && uri.startsWith("/api/v1/delitos")) {
                return true;
            }

            // Ver tipos y subtipos de delito (solo lectura)
            if (method.equals("GET") && (
                    uri.startsWith("/api/v1/tipos-delito") ||
                            uri.startsWith("/api/v1/subtipos-delito")
            )) {
                return true;
            }

            // Ver usuarios (solo lectura)
            if (method.equals("GET") && uri.startsWith("/api/v1/usuarios")) {
                return true;
            }

            // Ver localizaciones (solo lectura)
            if (method.equals("GET") && uri.startsWith("/api/v1/localizaciones")) {
                return true;
            }

            // Ver involucrados (solo lectura)
            if (method.equals("GET") && uri.startsWith("/api/v1/involucrados")) {
                return true;
            }

            // PROHIBIDO: Registrar expediente
            if (method.equals("POST") && uri.equals("/api/v1/expedientes")) {
                return false;
            }

            // PROHIBIDO: Registrar expediente vía el adaptador de incidentes
            if (method.equals("POST") && uri.equals("/api/v1/incidentes")) {
                return false;
            }

            // PROHIBIDO: Sellar expediente
            if (method.equals("PATCH") && uri.matches("^/api/v1/expedientes/\\d+/sellar$")) {
                return false;
            }

            // Puede agrupar expedientes en un caso (POST) — HU1 "Agrupar delitos en un caso"
            if (method.equals("POST") && uri.equals("/api/v1/casos")) {
                return true;
            }

            // El resto de operaciones de escritura sobre casos permanece prohibido
            if (uri.startsWith("/api/v1/casos") && !method.equals("GET")) {
                return false;
            }

            return !(uri.startsWith("/api/v1/usuarios") && !method.equals("GET"));
        }

        return false;
    }

    private String generarMensajeError(String rol) {
        if ("OFICIAL".equalsIgnoreCase(rol)) {
            return "Los Oficiales solo pueden: " +
                    "Ver y registrar expedientes, " +
                    "Sellar expedientes, " +
                    "Ver casos, delitos y usuarios. " +
                    "NO pueden acceder a Escenas, Evidencias o Modus Operandi.";
        }
        if ("ANALISTA".equalsIgnoreCase(rol)) {
            return "Los Analistas solo pueden: " +
                    "Gestionar Escenas, Evidencias, Modus Operandi y Casos, " +
                    "Ver expedientes, delitos y usuarios. " +
                    "NO pueden registrar o sellar expedientes.";
        }
        return "No tiene permisos para acceder a este recurso.";
    }

    private boolean rechazar(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + mensaje + "\"}");
        response.getWriter().flush();
        return false;
    }
}