package com.guardia.core;

import com.guardia.core.model.Expediente;
import org.springframework.context.ApplicationEvent;

public class SelloExpedienteEvent extends ApplicationEvent {
    private final Expediente expediente;

    public SelloExpedienteEvent(Object source, Expediente expediente) {
        super(source);
        this.expediente = expediente;
    }

    public Expediente getExpediente() { return expediente; }
}