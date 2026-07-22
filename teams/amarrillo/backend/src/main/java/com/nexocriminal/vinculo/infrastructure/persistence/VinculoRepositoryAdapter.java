package com.nexocriminal.vinculo.infrastructure.persistence;

import com.nexocriminal.domain.vinculo.VinculoRepository;
import com.nexocriminal.vinculo.domain.model.Vinculo;
import com.nexocriminal.vinculo.domain.port.VinculoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/** Adapter: implementa el puerto usando el VinculoRepository JPA existente. */
@Component
public class VinculoRepositoryAdapter implements VinculoRepositoryPort {

    private final VinculoRepository jpaRepository;
    private final VinculoMapper mapper;

    public VinculoRepositoryAdapter(VinculoRepository jpaRepository, VinculoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Vinculo> findAllActivos() {
        return jpaRepository.findAllActivos().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vinculo> findByNodo(String tipo, Long id) {
        return jpaRepository.findByNodo(tipo, id).stream().map(mapper::toDomain).toList();
    }
}