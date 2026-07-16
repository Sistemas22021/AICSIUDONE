package com.nexocriminal.ubicacion.application;

import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: obtener una ubicacion por id. */
@Service
public class GetUbicacion {

    private final UbicacionRepositoryPort repository;

    public GetUbicacion(UbicacionRepositoryPort repository) {
        this.repository = repository;
    }

    public Ubicacion execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ubicacion no encontrada: " + id));
    }
}