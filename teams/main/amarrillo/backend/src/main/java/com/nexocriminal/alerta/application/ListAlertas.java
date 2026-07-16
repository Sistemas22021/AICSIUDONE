package com.nexocriminal.alerta.application;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.alerta.domain.port.AlertaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar todas las alertas. */
@Service
public class ListAlertas {

    private final AlertaRepositoryPort repository;

    public ListAlertas(AlertaRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Alerta> execute() {
        return repository.findAll();
    }
}