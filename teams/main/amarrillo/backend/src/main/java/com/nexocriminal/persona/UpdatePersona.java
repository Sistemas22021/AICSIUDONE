package com.nexocriminal.persona.application;

import com.nexocriminal.domain.persona.RolPersona;
import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** Caso de uso: actualizar los datos de una persona. */
@Service
public class UpdatePersona {

    private final PersonaRepositoryPort repository;

    public UpdatePersona(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    public Persona execute(Long id, String nombre, String apellido, String alias,
                           LocalDate fechaNacimiento, RolPersona rol, String telefono) {
        Persona existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + id));
        existente.actualizarDatos(nombre, apellido, alias, fechaNacimiento, rol, telefono);
        return repository.save(existente);
    }
}