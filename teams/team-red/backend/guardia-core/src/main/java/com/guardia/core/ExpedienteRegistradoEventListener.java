package com.guardia.core;

import com.guardia.core.service.DeteccionModusOperandiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class ExpedienteRegistradoEventListener {

    private static final Logger log = LoggerFactory.getLogger(ExpedienteRegistradoEventListener.class);
    private final DeteccionModusOperandiService deteccionModusOperandiService;

    public ExpedienteRegistradoEventListener(DeteccionModusOperandiService deteccionModusOperandiService) {
        this.deteccionModusOperandiService = deteccionModusOperandiService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onExpedienteRegistrado(ExpedienteRegistradoEvent event) {
        log.info("[MO] Disparando análisis asíncrono de Modus Operandi para expediente id={}",
                event.getExpedienteId());
        deteccionModusOperandiService.analizarPatrones(event.getExpedienteId());
    }
}