package com.nexocriminal.suceso.application;

import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.suceso.domain.model.Suceso;
import com.nexocriminal.suceso.domain.port.SucesoRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Caso de uso: actualizar los campos simples de un suceso (fecha, modus,
 * descripcion, tipo). Las relaciones (vehiculo, victima, ubicacion) NO se
 * modifican aca: se preservan las del suceso existente.
 */
@Service
public class UpdateSuceso {

    private final SucesoRepositoryPort repository;

    public UpdateSuceso(SucesoRepositoryPort repository) {
        this.repository = repository;
    }

    public Suceso execute(Long id, TipoSuceso tipo, LocalDateTime fechaHora,
                          String modusOperandi, String descripcion) {
        Suceso existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suceso no encontrado: " + id));

        // Reconstruir preservando las relaciones existentes (vehiculo, victima, ubicaciones)
        Suceso actualizado = new Suceso(
                existente.getId(),
                tipo != null ? tipo : existente.getTipo(),
                fechaHora != null ? fechaHora : existente.getFechaHora(),
                modusOperandi,
                descripcion,
                existente.getVehiculoId(),
                existente.getVictimaId(),
                existente.getUbicacionId(),
                existente.getUbicacionUltimaId(),
                existente.getCreadoEn()
        );
        return repository.save(actualizado);
    }
}