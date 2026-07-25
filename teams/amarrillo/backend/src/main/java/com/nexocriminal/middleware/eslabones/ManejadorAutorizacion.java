package com.nexocriminal.middleware.eslabones;

import com.nexocriminal.middleware.ContextoPeticion;
import com.nexocriminal.middleware.ManejadorPeticion;
import lombok.extern.slf4j.Slf4j;

/**
 * Eslabon 2: autorizacion por rol.
 *
 * Matriz de permisos:
 *   ADMIN      -> acceso total.
 *   ANALISTA   -> consulta, creacion y edicion. No elimina ni gestiona usuarios.
 *   SUPERVISOR -> consulta y validacion de alertas (cambiar su estado).
 *   AUDITOR    -> solo lectura.
 *
 * La autorizacion se valida SIEMPRE aqui (backend). Ocultar botones en el
 * frontend es solo experiencia de usuario, no seguridad.
 */
@Slf4j
public class ManejadorAutorizacion extends ManejadorPeticion {

    private static final String ADMIN      = "ADMIN";
    private static final String ANALISTA   = "ANALISTA";
    private static final String SUPERVISOR = "SUPERVISOR";
    private static final String AUDITOR    = "AUDITOR";

    private static final String RUTA_USUARIOS = "/api/v1/usuarios";
    private static final String RUTA_ALERTAS  = "/api/v1/alertas";

    @Override
    public void manejar(ContextoPeticion contexto) {
        String metodo = contexto.getMetodo();
        String path   = contexto.getPath();
        String rol    = contexto.getRol() != null ? contexto.getRol().toUpperCase() : "";

        boolean esLectura = "GET".equalsIgnoreCase(metodo);

        // 1. ADMIN: acceso total, no se evalua nada mas.
        if (ADMIN.equals(rol)) {
            log.debug("[CADENA] ADMIN autorizado en {} {}", metodo, path);
            manejarSiguiente(contexto);
            return;
        }

        // 2. La gestion de usuarios es exclusiva de ADMIN.
        if (path.startsWith(RUTA_USUARIOS)) {
            log.warn("[CADENA] Acceso denegado a {} {} para rol {}", metodo, path, rol);
            contexto.rechazar(403, "No autorizado: la gestion de usuarios requiere rol ADMIN");
            return;
        }

        // 3. Eliminar solo lo puede hacer ADMIN.
        if ("DELETE".equalsIgnoreCase(metodo)) {
            contexto.rechazar(403, "No autorizado: eliminar registros requiere rol ADMIN");
            return;
        }

        // 4. AUDITOR: solo lectura.
        if (AUDITOR.equals(rol)) {
            if (esLectura) {
                manejarSiguiente(contexto);
            } else {
                contexto.rechazar(403, "No autorizado: el rol AUDITOR es de solo lectura");
            }
            return;
        }

        // 5. SUPERVISOR: lectura + validacion de alertas.
        if (SUPERVISOR.equals(rol)) {
            boolean validaAlerta = path.startsWith(RUTA_ALERTAS)
                    && ("PATCH".equalsIgnoreCase(metodo) || "PUT".equalsIgnoreCase(metodo));
            if (esLectura || validaAlerta) {
                manejarSiguiente(contexto);
            } else {
                contexto.rechazar(403,
                        "No autorizado: el rol SUPERVISOR solo consulta y valida alertas");
            }
            return;
        }

        // 6. ANALISTA: lectura, creacion y edicion (el DELETE ya fue bloqueado arriba).
        if (ANALISTA.equals(rol)) {
            manejarSiguiente(contexto);
            return;
        }

        // 7. Rol desconocido o vacio.
        log.warn("[CADENA] Rol no reconocido '{}' en {} {}", rol, metodo, path);
        contexto.rechazar(403, "No autorizado: rol no reconocido");
    }
}