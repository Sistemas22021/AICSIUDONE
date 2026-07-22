package com.nexocriminal.ubicacion.application;

import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: eliminar una ubicacion. */
@Service
public class DeleteUbicacion {

    private final UbicacionRepositoryPort repository;

    public DeleteUbicacion(UbicacionRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.deleteById(id);
    }
}