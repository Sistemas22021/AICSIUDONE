package com.nexocriminal.alerta.application;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.alerta.domain.port.AlertaRepositoryPort;
import com.nexocriminal.domain.alerta.EstadoAlerta;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar alertas pendientes. */
@Service
public class ListAlertasPendientes {

    private final AlertaRepositoryPort repository;

    public ListAlertasPendientes(AlertaRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Alerta> execute() {
        return repository.findByEstado(EstadoAlerta.PENDIENTE);
    }
}