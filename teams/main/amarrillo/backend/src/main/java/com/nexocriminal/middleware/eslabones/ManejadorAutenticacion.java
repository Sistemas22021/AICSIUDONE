package com.nexocriminal.middleware.eslabones;

import com.nexocriminal.middleware.ContextoPeticion;
import com.nexocriminal.middleware.ManejadorPeticion;
import com.nexocriminal.security.JwtService;
import lombok.extern.slf4j.Slf4j;

/**
 * Eslabon 1: verifica que la peticion tenga un JWT valido (usuario logueado).
 */
@Slf4j
public class ManejadorAutenticacion extends ManejadorPeticion {

    private final JwtService jwtService;

    public ManejadorAutenticacion(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void manejar(ContextoPeticion contexto) {
        String authHeader = contexto.getRequest().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[CADENA] Peticion sin token a {}", contexto.getPath());
            contexto.rechazar(401, "No autenticado: falta token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Ajustar a los metodos reales de tu JwtService
            String username = jwtService.extraerUsername(token);
            String rol = jwtService.extraerRol(token);

            if (username == null || !jwtService.validar(token)) {
                contexto.rechazar(401, "Token invalido o expirado");
                return;
            }

            contexto.setUsername(username);
            contexto.setRol(rol);
            contexto.setAutenticado(true);
            log.debug("[CADENA] Autenticado: {} (rol {})", username, rol);

        } catch (Exception e) {
            contexto.rechazar(401, "Token invalido: " + e.getMessage());
            return;
        }

        manejarSiguiente(contexto);
    }
}