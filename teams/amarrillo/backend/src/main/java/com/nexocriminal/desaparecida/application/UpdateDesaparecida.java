package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Caso de uso: actualizar los datos de una desaparecida. */
@Service
public class UpdateDesaparecida {

    private final DesaparecidaRepositoryPort repository;

    public UpdateDesaparecida(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public Desaparecida execute(Long id, String nombre, String apellido, String alias,
                                LocalDate fechaNacimiento, String genero, Integer estaturaCm, Integer pesoKg,
                                String contextura, String colorCabello, String colorOjos, String senasParticulares,
                                String ropaAlDesaparecer, LocalDateTime fechaDesaparicion, Long ultimaUbicacionId,
                                String circunstancias, String reportanteNombre, String reportanteTelefono,
                                String reportanteRelacion, EstadoDesaparicion estado, PrioridadDesaparicion prioridad) {
        Desaparecida existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona desaparecida no encontrada: " + id));
        existente.actualizarDatos(nombre, apellido, alias, fechaNacimiento, genero, estaturaCm, pesoKg,
                contextura, colorCabello, colorOjos, senasParticulares, ropaAlDesaparecer, fechaDesaparicion,
                ultimaUbicacionId, circunstancias, reportanteNombre, reportanteTelefono, reportanteRelacion,
                estado, prioridad);
        return repository.save(existente);
    }
}