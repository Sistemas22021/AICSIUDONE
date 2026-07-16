package com.nexocriminal.ubicacion.application;

import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: crear una ubicacion. */
@Service
public class CreateUbicacion {

    private final UbicacionRepositoryPort repository;

    public CreateUbicacion(UbicacionRepositoryPort repository) {
        this.repository = repository;
    }

    public Ubicacion execute(Ubicacion ubicacion) {
        return repository.save(ubicacion);
    }
}