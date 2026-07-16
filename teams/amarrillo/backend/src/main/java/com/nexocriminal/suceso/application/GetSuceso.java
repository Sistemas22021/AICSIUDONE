package com.nexocriminal.suceso.application;

import com.nexocriminal.suceso.domain.model.Suceso;
import com.nexocriminal.suceso.domain.port.SucesoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: obtener un suceso por id. */
@Service
public class GetSuceso {

    private final SucesoRepositoryPort repository;

    public GetSuceso(SucesoRepositoryPort repository) {
        this.repository = repository;
    }

    public Suceso execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suceso no encontrado: " + id));
    }
}