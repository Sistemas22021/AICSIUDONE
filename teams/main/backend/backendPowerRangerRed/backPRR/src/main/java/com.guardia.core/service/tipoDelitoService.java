package com.guardia.core.service;

import com.guardia.core.dto.TipoDelitoDto;
import com.guardia.core.repository.TipoDelitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TipoDelitoService {
    @Autowired
    private TipoDelitoRepository repository;

    public List<TipoDelitoDto> listarParaDesplegable() {
        return repository.findAll().stream().map(tipo -> new TipoDelitoDto(tipo.getId(), tipo.getNombre())).toList();
    }

    public String obtenerNombreDelito(TipoDelitoDto tipoDelitoDto) {
        return tipoDelitoDto.nombre();
    }
}