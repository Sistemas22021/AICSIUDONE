package com.guardia.core;

import com.guardia.core.model.Escena;
import org.springframework.context.ApplicationEvent;

public class EscenaLiberadaEvent extends ApplicationEvent {
    private final Escena escena;

    public EscenaLiberadaEvent(Object source, Escena escena) {
        super(source);
        this.escena = escena;
    }

    public Escena getEscena() { return escena; }
}