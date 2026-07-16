package com.nexocriminal.middleware;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Objeto que viaja a traves de la Cadena de Responsabilidad.
 * Cada eslabon lee/escribe informacion aqui.
 */
@Getter
@Setter
public class ContextoPeticion {

    private final HttpServletRequest request;
    private final String path;
    private final String metodo;

    // Se llenan a medida que la cadena avanza
    private String username;
    private String rol;
    private boolean autenticado = false;

    // Si algun eslabon rechaza, setea esto
    private boolean rechazado = false;
    private int codigoError;
    private String mensajeError;

    public ContextoPeticion(HttpServletRequest request) {
        this.request = request;
        this.path = request.getRequestURI();
        this.metodo = request.getMethod();
    }

    public void rechazar(int codigo, String mensaje) {
        this.rechazado = true;
        this.codigoError = codigo;
        this.mensajeError = mensaje;
    }
}