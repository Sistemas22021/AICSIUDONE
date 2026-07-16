package com.nexocriminal.persona.application;

import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: obtener una persona por id. */
@Service
public class GetPersona {

    private final PersonaRepositoryPort repository;

    public GetPersona(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    public Persona execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + id));
    }
}