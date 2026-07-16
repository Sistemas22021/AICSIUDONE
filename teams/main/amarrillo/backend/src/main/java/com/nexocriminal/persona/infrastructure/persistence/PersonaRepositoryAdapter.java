package com.nexocriminal.persona.infrastructure.persistence;

import com.nexocriminal.domain.persona.PersonaRepository;
import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adapter: implementa el puerto del dominio usando el JpaRepository existente. */
@Component
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaRepository jpaRepository;
    private final PersonaMapper mapper;

    public PersonaRepositoryAdapter(PersonaRepository jpaRepository, PersonaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Persona save(Persona persona) {
        var entity = mapper.toEntity(persona);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Persona> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Persona> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Persona> findByDocumento(String documento) {
        return jpaRepository.findByDocumento(documento).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Persona> findIntermediarios(Long victimaId, Long sospechosoId) {
        return jpaRepository.encontrarIntermediarios(victimaId, sospechosoId)
                .stream().map(mapper::toDomain).toList();
    }
}