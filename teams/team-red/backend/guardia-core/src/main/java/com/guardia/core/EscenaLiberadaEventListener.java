package com.guardia.core;

import com.guardia.core.service.NotificacionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EscenaLiberadaEventListener {

    private final NotificacionService notificacionService;

    public EscenaLiberadaEventListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @EventListener
    public void onEscenaLiberada(EscenaLiberadaEvent event) {
        notificacionService.notificarLiberacionEscena(event.getEscena());
    }
}