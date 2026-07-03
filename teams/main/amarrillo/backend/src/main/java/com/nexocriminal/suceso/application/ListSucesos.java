package com.nexocriminal.suceso.application;

import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.suceso.domain.model.Suceso;
import com.nexocriminal.suceso.domain.port.SucesoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar sucesos, opcionalmente por tipo. */
@Service
public class ListSucesos {

    private final SucesoRepositoryPort repository;

    public ListSucesos(SucesoRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Suceso> execute(TipoSuceso tipo) {
        return (tipo != null) ? repository.findByTipo(tipo) : repository.findAll();
    }
}