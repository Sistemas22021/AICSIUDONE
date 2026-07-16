package com.nexocriminal.vinculo.application;

import com.nexocriminal.vinculo.domain.model.Vinculo;
import com.nexocriminal.vinculo.domain.port.VinculoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar todos los vinculos activos. */
@Service
public class ListVinculosActivos {

    private final VinculoRepositoryPort repository;

    public ListVinculosActivos(VinculoRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Vinculo> execute() {
        return repository.findAllActivos();
    }
}