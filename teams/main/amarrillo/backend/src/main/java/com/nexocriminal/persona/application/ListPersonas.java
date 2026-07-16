package com.nexocriminal.persona.application;

import com.nexocriminal.domain.persona.RolPersona;
import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar personas, opcionalmente filtradas por rol. */
@Service
public class ListPersonas {

    private final PersonaRepositoryPort repository;

    public ListPersonas(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    /** Sin filtro: todas las personas. */
    public List<Persona> execute() {
        return repository.findAll();
    }

    /** Con filtro opcional por rol. Si rol es null, devuelve todas. */
    public List<Persona> execute(RolPersona rol) {
        if (rol == null) {
            return repository.findAll();
        }
        return repository.findAll().stream()
                .filter(p -> p.getRol() == rol)
                .toList();
    }
}