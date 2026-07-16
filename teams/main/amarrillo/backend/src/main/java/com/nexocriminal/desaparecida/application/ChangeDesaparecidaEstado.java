package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import org.springframework.stereotype.Service;

/** Caso de uso: cambiar el estado de una desaparecida. */
@Service
public class ChangeDesaparecidaEstado {

    private final DesaparecidaRepositoryPort repository;

    public ChangeDesaparecidaEstado(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public Desaparecida execute(Long id, EstadoDesaparicion estado) {
        Desaparecida d = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona desaparecida no encontrada: " + id));
        d.cambiarEstado(estado);
        return repository.save(d);
    }
}