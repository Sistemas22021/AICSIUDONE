package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: buscar desaparecidas con ultima ubicacion dentro de un radio. */
@Service
public class BuscarCercanas {

    private final DesaparecidaRepositoryPort repository;

    public BuscarCercanas(DesaparecidaRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Desaparecida> execute(double lat, double lng, int radioMetros) {
        return repository.findEnRadio(lat, lng, radioMetros);
    }
}