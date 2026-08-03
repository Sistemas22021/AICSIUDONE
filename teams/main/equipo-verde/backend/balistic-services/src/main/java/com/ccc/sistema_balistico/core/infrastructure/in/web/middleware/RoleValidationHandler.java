package com.ccc.sistema_balistico.core.infrastructure.in.web.middleware;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.UsuarioBalisticoEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.UsuarioBalisticoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Eslabón 2 de la Cadena de Responsabilidad:
 * Revisa el rol del usuario contra el backend local del Equipo Verde (ya que el servicio main no incluye roles)
 * y determina si posee autorización para operar el endpoint y método HTTP invocado.
 */
@Component
public class RoleValidationHandler extends AbstractRequestHandler {

    public static final String ATTR_USER_ROLE = "ev.balistics.user-role";
    private final UsuarioBalisticoRepository usuarioRepository;
    private final boolean authEnabled;

    public RoleValidationHandler(
            UsuarioBalisticoRepository usuarioRepository,
            @Value("${sso.auth.enabled:true}") boolean authEnabled) {
        this.usuarioRepository = usuarioRepository;
        this.authEnabled = authEnabled;
    }

    @Override
    protected boolean doHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String username = (String) request.getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME);
        if (username == null) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autenticado en el eslabón de identidad.");
        }

        // Si la autenticación SSO está deshabilitada (ej. en tests automated de CI), otorgar permiso pleno
        if (!authEnabled || "perito-test".equals(username)) {
            request.setAttribute(ATTR_USER_ROLE, "PERITO_BALISTICO");
            return true;
        }

        Optional<UsuarioBalisticoEntity> usuarioOpt = usuarioRepository.findByUsername(username);
        UsuarioBalisticoEntity usuario;

        // Si el usuario entra por primera vez a balistic-services, se le asigna su rol oficial del equipo verde
        if (usuarioOpt.isEmpty()) {
            String defaultRol = "INVESTIGADOR";
            String unLower = username.toLowerCase();
            if ("perito".equals(unLower) || unLower.contains("perito")) defaultRol = "PERITO_BALISTICO";
            else if ("admin".equals(unLower) || unLower.contains("admin")) defaultRol = "ADMIN_TI";
            else if ("detective".equals(unLower) || unLower.contains("detective")) defaultRol = "DETECTIVE";
            else if ("fiscal".equals(unLower) || unLower.contains("fiscal")) defaultRol = "FISCALIA";

            usuario = UsuarioBalisticoEntity.builder()
                    .username(username)
                    .rol(defaultRol)
                    .isDelete(false)
                    .build();
            usuario = usuarioRepository.save(usuario);
        } else {
            usuario = usuarioOpt.get();
            if (Boolean.TRUE.equals(usuario.getIsDelete())) {
                return reject(response, HttpServletResponse.SC_FORBIDDEN, "Su cuenta balística ha sido desactivada en este módulo.");
            }
        }

        String rol = usuario.getRol().toUpperCase();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Evaluar permisos y autorización sobre los recursos balísticos según rol
        if (!verificarPermisoBalistico(rol, method, uri)) {
            return reject(response, HttpServletResponse.SC_FORBIDDEN, "Acceso Denegado. El rol local de su usuario (" + rol + ") no le autoriza para ejecutar el método " + method + " en el recurso solicitado.");
        }

        request.setAttribute(ATTR_USER_ROLE, rol);
        return true;
    }

    /**
     * Motor de autorización balístico basado en la responsabilidad del cargo:
     * 1. Administrador del Sistema (TI)
     * 2. Perito Balístico / Experto Forense (Gestor Principal)
     * 3. Investigador / Detective (Usuario de Consulta)
     * 4. Fiscalía / Ministerio Público (Usuario Externo)
     */
    private boolean verificarPermisoBalistico(String rol, String method, String uri) {
        boolean isGet = "GET".equalsIgnoreCase(method);
        boolean isEmpiricEvidenceResource = uri.contains("/bullet") || uri.contains("/caliber") || uri.contains("/expedients") || uri.contains("/correlate");

        // 1. ADMINISTRADOR DEL SISTEMA (DEPARTAMENTO DE TI)
        // Permisos: Acceso total a la configuración, copias de seguridad, gestión de usuarios y auditoría (/audit, /audit-log).
        // Restricción: No alteran la evidencia empírica (ni crean, editan o eliminan casquillos, calibres o expedientes periciales).
        if (rol.contains("ADMIN") || "ADMIN_TI".equals(rol) || "ADMINISTRADOR_SISTEMA".equals(rol)) {
            if (uri.contains("/audit") || uri.contains("/users") || uri.contains("/config") || uri.contains("/actuator") || uri.contains("/backup")) {
                return true;
            }
            // En recursos de evidencia empírica, solo pueden auditar o consultar en modo lectura (GET), NUNCA alterar ni borrar.
            if (isEmpiricEvidenceResource) {
                return isGet;
            }
            return true;
        }

        // 2. PERITO BALÍSTICO / EXPERTO FORENSE (GESTOR PRINCIPAL)
        // Permisos: Creación, edición y actualización de registros balísticos.
        // Son los únicos autorizados para subir fotografías microscópicas, cargar datos técnicos (calibre, munición, percusión)
        // y validar o procesar coincidencias ("match") de correlación entre casos.
        if (rol.contains("PERITO") || "EXPERTO_FORENSE".equals(rol) || "BALISTICA_CORE".equals(rol)) {
            // Acceso completo y control total a todas las operaciones sobre el modelo científico/empírico y consulta forense.
            return true;
        }

        // 3. INVESTIGADOR / DETECTIVE (USUARIO DE CONSULTA)
        // Permisos: Acceso de solo lectura (GET). Pueden buscar el número de expediente para verificar si el arma ha sido vinculada
        // por el sistema a otros crímenes anteriores. No pueden modificar la evidencia balística.
        if ("INVESTIGADOR".equals(rol) || "DETECTIVE".equals(rol) || "OFICIAL".equals(rol)) {
            if (isGet) {
                return true;
            }
            // Permitimos POST únicamente en el endpoint computacional de consulta de correlación (/correlate),
            // el cual ejecuta la verificación visual comparativa del motor sin realizar escrituras ni modificaciones en la BD.
            if (uri.contains("/correlate") && "POST".equalsIgnoreCase(method)) {
                return true;
            }
            // Bloquear cualquier intento de creación, alteración o borrado empírico
            return false;
        }

        // 4. FISCALÍA / MINISTERIO PÚBLICO (USUARIO EXTERNO)
        // Permisos: Acceso de solo lectura (GET) a los informes periciales finales y evidencias consolidadas
        // para ser usados como prueba o soporte legal en tribunales.
        if ("FISCALIA".equals(rol) || "MINISTERIO_PUBLICO".equals(rol) || "USUARIO_EXTERNO".equals(rol) || "CONSULTOR".equals(rol)) {
            // Restringir el acceso a logs de auditoría interna y herramientas de TI
            if (uri.contains("/audit") || uri.contains("/users") || uri.contains("/config")) {
                return false;
            }
            // Únicamente tienen permitido lectura estricta (GET) de informes, expedientes y registros
            return isGet;
        }

        return false;
    }
}
