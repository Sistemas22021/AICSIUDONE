package com.nexocriminal.vehiculo.domain.port;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: lo que el dominio de vehiculo necesita de la persistencia. */
public interface VehiculoRepositoryPort {
    Vehiculo save(Vehiculo vehiculo);
    List<Vehiculo> findAll();
    Optional<Vehiculo> findById(Long id);
    Optional<Vehiculo> findByPlaca(String placa);
    List<Vehiculo> findByEstado(EstadoVehiculo estado);
    void deleteById(Long id);
}