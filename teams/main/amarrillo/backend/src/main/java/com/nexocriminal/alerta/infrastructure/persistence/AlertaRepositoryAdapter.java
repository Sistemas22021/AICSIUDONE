package com.nexocriminal.alerta.infrastructure.persistence;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.alerta.domain.port.AlertaRepositoryPort;
import com.nexocriminal.domain.alerta.AlertaRepository;
import com.nexocriminal.domain.alerta.EstadoAlerta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adapter: implementa el puerto usando el AlertaRepository JPA existente. */
@Component
public class AlertaRepositoryAdapter implements AlertaRepositoryPort {

    private final AlertaRepository jpaRepository;
    private final AlertaMapper mapper;

    public AlertaRepositoryAdapter(AlertaRepository jpaRepository, AlertaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Alerta> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Alerta> findByEstado(EstadoAlerta estado) {
        return jpaRepository.findByEstado(estado).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Alerta> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Alerta save(Alerta alerta) {
        var entity = mapper.toEntity(alerta);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}