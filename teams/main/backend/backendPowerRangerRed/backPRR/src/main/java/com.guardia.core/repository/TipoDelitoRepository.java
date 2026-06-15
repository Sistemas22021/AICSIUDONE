package com.guardia.core.repository;

import com.guardia.core.model.TipoDelito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repositorio JPA para tipos de delito.
 * Incluye consultas por nombre y filtrado por si requieren subtipo.
 */
public interface TipoDelitoRepository extends JpaRepository<TipoDelito, Long> {
    Optional<TipoDelito> findByNombre(String nombre);
    List<TipoDelito> findByRequiereSubtipo(Boolean requiereSubtipo);
    boolean existsByNombre(String nombre);
}
