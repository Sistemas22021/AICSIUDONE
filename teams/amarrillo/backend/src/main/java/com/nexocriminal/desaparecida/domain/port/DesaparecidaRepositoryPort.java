package com.nexocriminal.desaparecida.domain.port;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para desaparecidas. */
public interface DesaparecidaRepositoryPort {
    Desaparecida save(Desaparecida d);
    List<Desaparecida> findAll();
    Optional<Desaparecida> findById(Long id);
    List<Desaparecida> findByEstado(EstadoDesaparicion estado);
    List<Desaparecida> findByPrioridad(PrioridadDesaparicion prioridad);
    List<Desaparecida> findEnRadio(double lat, double lng, int radioMetros);
    void deleteById(Long id);
}