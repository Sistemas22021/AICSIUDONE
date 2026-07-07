package com.nexocriminal.suceso.domain.port;

import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.suceso.domain.model.Suceso;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para sucesos. */
public interface SucesoRepositoryPort {
    Suceso save(Suceso suceso);
    List<Suceso> findAll();
    Optional<Suceso> findById(Long id);
    List<Suceso> findByTipo(TipoSuceso tipo);
    void deleteById(Long id);
}