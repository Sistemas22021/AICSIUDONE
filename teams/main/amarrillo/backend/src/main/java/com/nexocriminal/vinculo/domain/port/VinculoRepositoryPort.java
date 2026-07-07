package com.nexocriminal.vinculo.domain.port;

import com.nexocriminal.vinculo.domain.model.Vinculo;

import java.util.List;

/** Puerto de salida para vinculos (solo lectura desde el API). */
public interface VinculoRepositoryPort {
    List<Vinculo> findAllActivos();
    List<Vinculo> findByNodo(String tipo, Long id);
}