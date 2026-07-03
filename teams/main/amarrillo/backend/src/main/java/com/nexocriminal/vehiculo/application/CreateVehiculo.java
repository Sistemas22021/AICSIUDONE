package com.nexocriminal.vehiculo.application;

import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: crear un vehiculo. Valida placa unica. */
@Service
public class CreateVehiculo {

    private final VehiculoRepositoryPort repository;

    public CreateVehiculo(VehiculoRepositoryPort repository) {
        this.repository = repository;
    }

    public Vehiculo execute(Vehiculo vehiculo) {
        if (repository.findByPlaca(vehiculo.getPlaca()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehiculo con esa placa");
        }
        return repository.save(vehiculo);
    }
}