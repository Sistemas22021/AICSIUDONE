package com.nexocriminal.persona.application;

import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar todas las personas. */
@Service
public class ListPersonas {

    private final PersonaRepositoryPort repository;

    public ListPersonas(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Persona> execute() {
        return repository.findAll();
    }
}