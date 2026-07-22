package com.nexocriminal.ubicacion.infrastructure.persistence;

import com.nexocriminal.domain.ubicacion.UbicacionRepository;
import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.domain.port.UbicacionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter: implementa el puerto del dominio usando el JpaRepository existente.
 * Aca es donde el mundo de afuera (JPA) se conecta con lo que pide el dominio.
 */
@Component
public class UbicacionRepositoryAdapter implements UbicacionRepositoryPort {

    private final UbicacionRepository jpaRepository;
    private final UbicacionMapper mapper;

    public UbicacionRepositoryAdapter(UbicacionRepository jpaRepository, UbicacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Ubicacion save(Ubicacion ubicacion) {
        var entity = mapper.toEntity(ubicacion);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Ubicacion> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Ubicacion> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}