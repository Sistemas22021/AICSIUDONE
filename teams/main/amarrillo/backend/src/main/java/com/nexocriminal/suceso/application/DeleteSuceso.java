package com.nexocriminal.suceso.application;

import com.nexocriminal.suceso.domain.port.SucesoRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: eliminar un suceso. */
@Service
public class DeleteSuceso {

    private final SucesoRepositoryPort repository;

    public DeleteSuceso(SucesoRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.deleteById(id);
    }
}