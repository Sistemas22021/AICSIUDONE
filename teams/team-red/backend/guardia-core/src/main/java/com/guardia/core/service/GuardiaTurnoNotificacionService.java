package com.guardia.core.service;

import com.guardia.core.model.Escena;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Notifica al Guardia de turno cuando una escena se libera formalmente.
 * Implementación inicial vía log de auditoría (igual que SelloExpedienteEventListener);
 * se puede sustituir por correo/push/websocket sin tocar el resto del sistema.
 */
@Component
public class GuardiaTurnoNotificacionService implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(GuardiaTurnoNotificacionService.class);

    @Override
    public void notificarLiberacionEscena(Escena escena) {
        String folio = escena.getExpediente() != null ? escena.getExpediente().getFolio() : "N/A";
        String investigador = escena.getLiberadaPor() != null ? escena.getLiberadaPor().getFullName() : "N/A";

        log.info("[NOTIFICACION -> GUARDIA DE TURNO] Escena {} (expediente {}) liberada formalmente por {} el {}.",
                escena.getId(), folio, investigador, escena.getHoraLiberacion());
    }
}