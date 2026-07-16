package com.nexocriminal.alerta.application;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.alerta.domain.port.AlertaRepositoryPort;
import com.nexocriminal.domain.alerta.EstadoAlerta;
import org.springframework.stereotype.Service;

/** Caso de uso: cambiar el estado de una alerta. */
@Service
public class ChangeAlertaEstado {

    private final AlertaRepositoryPort repository;

    public ChangeAlertaEstado(AlertaRepositoryPort repository) {
        this.repository = repository;
    }

    public Alerta execute(Long id, EstadoAlerta estado) {
        Alerta a = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta no encontrada"));
        a.cambiarEstado(estado);
        return repository.save(a);
    }
}