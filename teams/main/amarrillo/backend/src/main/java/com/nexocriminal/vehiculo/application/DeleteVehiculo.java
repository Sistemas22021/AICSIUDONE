package com.nexocriminal.vehiculo.application;

import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: eliminar un vehiculo. */
@Service
public class DeleteVehiculo {

    private final VehiculoRepositoryPort repository;

    public DeleteVehiculo(VehiculoRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.deleteById(id);
    }
}