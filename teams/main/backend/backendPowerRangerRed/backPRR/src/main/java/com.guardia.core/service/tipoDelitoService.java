package com.guardia.core.service;

import com.guardia.core.model.tipoDelito;
import com.guardia.core.repository.tipoDelitoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class tipoDelitoService {
    private final tipoDelitoRepository repository;

    public tipoDelito guardar(tipoDelito delito) {
        return repository.save(delito);
    }

    public List<tipoDelito> listarTodos() {
        return repository.findAll();
    }
}