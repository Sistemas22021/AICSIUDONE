package com.guardia.core;

import com.guardia.core.SelloExpedienteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SelloExpedienteEventListener {

    private static final Logger log = LoggerFactory.getLogger(SelloExpedienteEventListener.class);

    @EventListener
    public void onSello(SelloExpedienteEvent event) {
        var e = event.getExpediente();
        log.info("[AUDITORIA] Expediente {} sellado | hash={} | agente={}",
                e.getFolio(), e.getHashIntegridad(), e.getAgenteSelladorInfo());
    }
}