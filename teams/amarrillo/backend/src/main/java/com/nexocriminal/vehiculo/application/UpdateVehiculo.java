package com.nexocriminal.vehiculo.application;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: actualizar los datos de un vehiculo. */
@Service
public class UpdateVehiculo {

    private final VehiculoRepositoryPort repository;

    public UpdateVehiculo(VehiculoRepositoryPort repository) {
        this.repository = repository;
    }

    public Vehiculo execute(Long id, String marca, String modelo, Integer anio,
                            String color, EstadoVehiculo estado, Long propietarioId) {
        Vehiculo existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + id));
        existente.actualizarDatos(marca, modelo, anio, color, estado, propietarioId);
        return repository.save(existente);
    }
}