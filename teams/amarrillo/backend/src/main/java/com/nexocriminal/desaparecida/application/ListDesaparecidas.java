package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar desaparecidas, opcionalmente por estado o prioridad. */
@Service
public class ListDesaparecidas {

    private final DesaparecidaRepositoryPort repository;

    public ListDesaparecidas(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Desaparecida> execute(EstadoDesaparicion estado, PrioridadDesaparicion prioridad) {
        if (estado != null) return repository.findByEstado(estado);
        if (prioridad != null) return repository.findByPrioridad(prioridad);
        return repository.findAll();
    }
}