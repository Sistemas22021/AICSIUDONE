package com.nexocriminal.persona.application;

import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: encontrar intermediarios entre una victima y un sospechoso
 * (camino de 2 grados en la red de relaciones). Envuelve el query recursivo.
 */
@Service
public class FindIntermediarios {

    private final PersonaRepositoryPort repository;

    public FindIntermediarios(PersonaRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Persona> execute(Long victimaId, Long sospechosoId) {
        return repository.findIntermediarios(victimaId, sospechosoId);
    }
}