package com.nexocriminal.ubicacion.application;

import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: marcar una ubicacion como nodo sospechoso.
 * Lo usara el motor Red Thread cuando lo migremos (hoy el motor usa el servicio viejo).
 */
@Service
public class MarkSuspiciousNode {

    private final UbicacionRepositoryPort repository;

    public MarkSuspiciousNode(UbicacionRepositoryPort repository) {
        this.repository = repository;
    }

    public Ubicacion execute(Long id) {
        Ubicacion u = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ubicacion no encontrada: " + id));
        u.marcarComoNodoSospechoso();
        return repository.save(u);
    }
}