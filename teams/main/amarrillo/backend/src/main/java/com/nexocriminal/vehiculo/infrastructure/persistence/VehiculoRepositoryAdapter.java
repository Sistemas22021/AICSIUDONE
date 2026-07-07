package com.nexocriminal.vehiculo.infrastructure.persistence;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.domain.vehiculo.VehiculoRepository;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adapter: implementa el puerto del dominio usando el JpaRepository existente. */
@Component
public class VehiculoRepositoryAdapter implements VehiculoRepositoryPort {

    private final VehiculoRepository jpaRepository;
    private final VehiculoMapper mapper;

    public VehiculoRepositoryAdapter(VehiculoRepository jpaRepository, VehiculoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        var entity = mapper.toEntity(vehiculo);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Vehiculo> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Vehiculo> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Vehiculo> findByPlaca(String placa) {
        return jpaRepository.findByPlaca(placa).map(mapper::toDomain);
    }

    @Override
    public List<Vehiculo> findByEstado(EstadoVehiculo estado) {
        return jpaRepository.findByEstado(estado).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}