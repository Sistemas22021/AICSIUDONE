package com.guardia.core.repository;

import com.guardia.core.model.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
    List<Evidencia> findByEscenaId(Long escenaId);
    Optional<Evidencia> findByNumeroItem(String numeroItem);
    List<Evidencia> findByTipo(String tipo);
}
