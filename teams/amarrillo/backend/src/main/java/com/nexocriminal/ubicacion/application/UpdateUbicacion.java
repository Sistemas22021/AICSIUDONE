package com.nexocriminal.ubicacion.application;

import com.nexocriminal.domain.ubicacion.TipoUbicacion;
import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: actualizar los datos de una ubicacion. */
@Service
public class UpdateUbicacion {

    private final UbicacionRepositoryPort repository;

    public UpdateUbicacion(UbicacionRepositoryPort repository) {
        this.repository = repository;
    }

    public Ubicacion execute(Long id, String direccion, double latitud, double longitud, TipoUbicacion tipo) {
        Ubicacion existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ubicacion no encontrada: " + id));
        existente.actualizarDatos(direccion, latitud, longitud, tipo);
        return repository.save(existente);
    }
}