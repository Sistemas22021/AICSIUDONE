package com.nexocriminal.vehiculo.application;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: cambiar el estado de un vehiculo. */
@Service
public class ChangeVehiculoEstado {

    private final VehiculoRepositoryPort repository;

    public ChangeVehiculoEstado(VehiculoRepositoryPort repository) {
        this.repository = repository;
    }

    public Vehiculo execute(Long id, EstadoVehiculo estado) {
        Vehiculo v = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + id));
        v.cambiarEstado(estado);
        return repository.save(v);
    }
}