package com.guardia.core.repository;

import com.guardia.core.model.SubtipoDelito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtipoDelitoRepository extends JpaRepository<SubtipoDelito, Long> {
    List<SubtipoDelito> findByTipoDelitoId(Long tipoDelitoId);
    boolean existsByNombreAndTipoDelitoId(String nombre, Long tipoDelitoId);
}
