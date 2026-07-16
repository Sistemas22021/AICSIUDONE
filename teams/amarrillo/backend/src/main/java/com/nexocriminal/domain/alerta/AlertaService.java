package com.nexocriminal.domain.alerta;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertaService {

    private final AlertaRepository repository;

    public Alerta crear(Alerta a) {
        return repository.save(a);
    }

    @Transactional(readOnly = true)
    public List<Alerta> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Alerta> pendientes() {
        return repository.findByEstado(EstadoAlerta.PENDIENTE);
    }

    public Alerta cambiarEstado(Long id, EstadoAlerta estado) {
        Alerta a = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta no encontrada"));
        a.setEstado(estado);
        return repository.save(a);
    }
}