package com.nexocriminal.alerta.domain.port;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.domain.alerta.EstadoAlerta;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para alertas (lectura y cambio de estado). */
public interface AlertaRepositoryPort {
    List<Alerta> findAll();
    List<Alerta> findByEstado(EstadoAlerta estado);
    Optional<Alerta> findById(Long id);
    Alerta save(Alerta alerta);
}