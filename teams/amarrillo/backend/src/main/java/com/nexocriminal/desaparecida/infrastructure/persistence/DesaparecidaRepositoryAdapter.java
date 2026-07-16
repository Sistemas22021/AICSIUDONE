package com.nexocriminal.desaparecida.infrastructure.persistence;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecidaRepository;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter: implementa el puerto usando el PersonaDesaparecidaRepository JPA.
 * Para guardar, preserva la entidad existente (y sus fotos) al actualizar.
 */
@Component
public class DesaparecidaRepositoryAdapter implements DesaparecidaRepositoryPort {

    private final PersonaDesaparecidaRepository jpaRepository;
    private final DesaparecidaMapper mapper;

    public DesaparecidaRepositoryAdapter(PersonaDesaparecidaRepository jpaRepository, DesaparecidaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Desaparecida save(Desaparecida d) {
        // Si tiene id, recuperamos la entidad existente para preservar fotos y campos no manejados.
        com.nexocriminal.domain.desaparecida.PersonaDesaparecida existente =
                (d.getId() != null) ? jpaRepository.findById(d.getId()).orElse(null) : null;
        var entity = mapper.toEntity(d, existente);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Desaparecida> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Desaparecida> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Desaparecida> findByEstado(EstadoDesaparicion estado) {
        return jpaRepository.findByEstado(estado).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Desaparecida> findByPrioridad(PrioridadDesaparicion prioridad) {
        return jpaRepository.findByPrioridad(prioridad).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Desaparecida> findEnRadio(double lat, double lng, int radioMetros) {
        return jpaRepository.findEnRadio(lat, lng, radioMetros).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}