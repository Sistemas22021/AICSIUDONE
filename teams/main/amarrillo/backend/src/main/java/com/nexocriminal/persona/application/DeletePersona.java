package com.nexocriminal.persona.application;

import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: eliminar una persona. */
@Service
public class DeletePersona {

    private final PersonaRepositoryPort repository;

    public DeletePersona(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.deleteById(id);
    }
}