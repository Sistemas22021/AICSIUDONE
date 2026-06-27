package com.nexocriminal.persona.application;

import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: crear una persona. Valida documento unico. */
@Service
public class CreatePersona {

    private final PersonaRepositoryPort repository;

    public CreatePersona(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    public Persona execute(Persona persona) {
        if (repository.findByDocumento(persona.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona con ese documento");
        }
        return repository.save(persona);
    }
}