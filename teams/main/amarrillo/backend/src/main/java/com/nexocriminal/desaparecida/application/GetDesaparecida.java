package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: obtener una desaparecida por id. */
@Service
public class GetDesaparecida {

    private final DesaparecidaRepositoryPort repository;

    public GetDesaparecida(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public Desaparecida execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona desaparecida no encontrada: " + id));
    }
}