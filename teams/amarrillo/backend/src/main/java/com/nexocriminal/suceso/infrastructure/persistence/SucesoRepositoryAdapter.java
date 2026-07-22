package com.nexocriminal.suceso.infrastructure.persistence;

import com.nexocriminal.domain.suceso.SucesoRepository;
import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.suceso.domain.model.Suceso;
import com.nexocriminal.suceso.domain.port.SucesoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adapter: implementa el puerto usando el SucesoRepository JPA existente. */
@Component
public class SucesoRepositoryAdapter implements SucesoRepositoryPort {

    private final SucesoRepository jpaRepository;
    private final SucesoMapper mapper;

    public SucesoRepositoryAdapter(SucesoRepository jpaRepository, SucesoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Suceso save(Suceso suceso) {
        var entity = mapper.toEntity(suceso);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Suceso> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Suceso> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Suceso> findByTipo(TipoSuceso tipo) {
        return jpaRepository.findByTipo(tipo).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}