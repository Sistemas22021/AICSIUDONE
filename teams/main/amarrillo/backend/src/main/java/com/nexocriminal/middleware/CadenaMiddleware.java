package com.nexocriminal.middleware;

import com.nexocriminal.middleware.eslabones.ManejadorAutenticacion;
import com.nexocriminal.middleware.eslabones.ManejadorAutorizacion;
import com.nexocriminal.security.JwtService;
import org.springframework.stereotype.Component;

/**
 * Arma la Cadena de Responsabilidad y la ejecuta.
 *
 * Orden de eslabones:
 *   1. Autenticacion (¿logueado?)
 *   2. Autorizacion  (¿rol correcto?)
 *
 * Agregar un nuevo eslabon (ej: rate limiting, auditoria) es trivial:
 * se crea la clase y se enlaza aqui. (Open/Closed)
 */
@Component
public class CadenaMiddleware {

    private final JwtService jwtService;

    public CadenaMiddleware(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public ContextoPeticion ejecutar(ContextoPeticion contexto) {
        // Construir la cadena
        ManejadorPeticion autenticacion = new ManejadorAutenticacion(jwtService);
        ManejadorPeticion autorizacion = new ManejadorAutorizacion();

        autenticacion.enlazarCon(autorizacion);

        // Disparar la cadena desde el primer eslabon
        autenticacion.manejar(contexto);

        return contexto;
    }
}