package com.nexocriminal.vehiculo.application;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar vehiculos, opcionalmente filtrando por estado. */
@Service
public class ListVehiculos {

    private final VehiculoRepositoryPort repository;

    public ListVehiculos(VehiculoRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Vehiculo> execute(EstadoVehiculo estado) {
        return (estado != null) ? repository.findByEstado(estado) : repository.findAll();
    }
}