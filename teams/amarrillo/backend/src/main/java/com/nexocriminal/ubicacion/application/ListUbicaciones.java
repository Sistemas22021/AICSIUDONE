package com.nexocriminal.ubicacion.application;

import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar todas las ubicaciones. */
@Service
public class ListUbicaciones {

    private final UbicacionRepositoryPort repository;

    public ListUbicaciones(UbicacionRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Ubicacion> execute() {
        return repository.findAll();
    }
}