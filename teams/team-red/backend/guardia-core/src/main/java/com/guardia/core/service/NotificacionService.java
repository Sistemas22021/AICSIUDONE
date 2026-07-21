package com.guardia.core.service;

import com.guardia.core.model.Escena;

/**
 * Puerto para notificar eventos forenses relevantes a otros actores del sistema.
 * Permite cambiar el canal (log, correo, push, websocket) sin tocar la lógica
 * de negocio que la invoca — Principio de Inversión de Dependencias (DIP).
 */
public interface NotificacionService {
    void notificarLiberacionEscena(Escena escena);
}