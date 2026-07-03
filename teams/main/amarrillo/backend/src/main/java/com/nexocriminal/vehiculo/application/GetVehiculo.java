package com.nexocriminal.vehiculo.application;

import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: obtener un vehiculo por id. */
@Service
public class GetVehiculo {

    private final VehiculoRepositoryPort repository;

    public GetVehiculo(VehiculoRepositoryPort repository) {
        this.repository = repository;
    }

    public Vehiculo execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + id));
    }
}