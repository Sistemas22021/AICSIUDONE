package com.guardia.core.repository;

import com.guardia.core.model.TipoDelito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDelitoRepository extends JpaRepository<TipoDelito, Long> {
    Optional<TipoDelito> findByNombre(String nombre);
    List<TipoDelito> findByRequiereSubtipo(Boolean requiereSubtipo);
    boolean existsByNombre(String nombre);
}
