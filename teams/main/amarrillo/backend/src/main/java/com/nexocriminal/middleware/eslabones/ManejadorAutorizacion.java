package com.nexocriminal.middleware.eslabones;

import com.nexocriminal.middleware.ContextoPeticion;
import com.nexocriminal.middleware.ManejadorPeticion;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Eslabon 2: verifica que el rol del usuario alcance para la ruta solicitada.
 *
 * Reglas simples: ciertas rutas requieren rol ADMIN; el resto, cualquier
 * usuario autenticado.
 */
@Slf4j
public class ManejadorAutorizacion extends ManejadorPeticion {

    // Rutas que SOLO puede tocar un ADMIN (ejemplos)
    private static final Map<String, List<String>> RUTAS_ADMIN = Map.of(
            "DELETE", List.of("/api/v1/personas", "/api/v1/usuarios"),
            "POST", List.of("/api/v1/usuarios")
    );

    @Override
    public void manejar(ContextoPeticion contexto) {
        String metodo = contexto.getMetodo();
        String path = contexto.getPath();
        String rol = contexto.getRol();

        boolean requiereAdmin = RUTAS_ADMIN.getOrDefault(metodo, List.of())
                .stream()
                .anyMatch(path::startsWith);

        if (requiereAdmin && !"ADMIN".equalsIgnoreCase(rol)) {
            log.warn("[CADENA] Acceso denegado a {} {} para rol {}", metodo, path, rol);
            contexto.rechazar(403, "No autorizado: se requiere rol ADMIN");
            return;
        }

        log.debug("[CADENA] Autorizado {} {} para rol {}", metodo, path, rol);
        manejarSiguiente(contexto);
    }
}