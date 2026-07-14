package com.guardia.core;

import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado inmediatamente después de guardar un nuevo expediente
 * (ver {@code ExpedienteServiceImpl#crear}), para que el análisis de Modus
 * Operandi (HU2) se dispare de forma asíncrona sin bloquear el registro.
 *
 * <p>Se transporta solo el id (no la entidad completa) porque el listener
 * procesa el evento en otro hilo/transacción: recargar el expediente por id
 * evita trabajar sobre una entidad detached del contexto de persistencia
 * original.</p>
 */
public class ExpedienteRegistradoEvent extends ApplicationEvent {

    private final Long expedienteId;

    public ExpedienteRegistradoEvent(Object source, Long expedienteId) {
        super(source);
        this.expedienteId = expedienteId;
    }

    public Long getExpedienteId() {
        return expedienteId;
    }
}