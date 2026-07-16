package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: eliminar una desaparecida. */
@Service
public class DeleteDesaparecida {

    private final DesaparecidaRepositoryPort repository;

    public DeleteDesaparecida(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.deleteById(id);
    }
}