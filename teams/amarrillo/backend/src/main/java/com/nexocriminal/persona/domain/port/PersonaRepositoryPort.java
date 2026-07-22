package com.nexocriminal.persona.domain.port;

import com.nexocriminal.persona.domain.model.Persona;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: lo que el dominio de persona necesita de la persistencia. */
public interface PersonaRepositoryPort {
    Persona save(Persona persona);
    List<Persona> findAll();
    Optional<Persona> findById(Long id);
    Optional<Persona> findByDocumento(String documento);
    void deleteById(Long id);
    List<Persona> findIntermediarios(Long victimaId, Long sospechosoId);
}