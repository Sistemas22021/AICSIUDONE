package com.nexocriminal.vinculo.application;

import com.nexocriminal.vinculo.domain.model.Vinculo;
import com.nexocriminal.vinculo.domain.port.VinculoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar vinculos conectados a un nodo (tipo + id). */
@Service
public class ListVinculosPorNodo {

    private final VinculoRepositoryPort repository;

    public ListVinculosPorNodo(VinculoRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Vinculo> execute(String tipo, Long id) {
        return repository.findByNodo(tipo.toUpperCase(), id);
    }
}