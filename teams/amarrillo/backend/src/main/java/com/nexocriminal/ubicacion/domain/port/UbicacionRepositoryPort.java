package com.nexocriminal.ubicacion.domain.port;

import com.nexocriminal.ubicacion.domain.model.Ubicacion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del dominio. Define QUE necesita el dominio de la persistencia,
 * sin saber COMO se implementa (JPA, Mongo, memoria, etc).
 * La infraestructura provee la implementacion (adapter).
 */
public interface UbicacionRepositoryPort {
    Ubicacion save(Ubicacion ubicacion);
    List<Ubicacion> findAll();
    Optional<Ubicacion> findById(Long id);
    void deleteById(Long id);
}