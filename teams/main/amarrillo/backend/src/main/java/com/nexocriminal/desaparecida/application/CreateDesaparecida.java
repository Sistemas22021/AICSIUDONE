package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import org.springframework.stereotype.Service;

/** Caso de uso: crear una persona desaparecida. */
@Service
public class CreateDesaparecida {

    private final DesaparecidaRepositoryPort repository;

    public CreateDesaparecida(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public Desaparecida execute(Desaparecida d) {
        return repository.save(d);
    }
}