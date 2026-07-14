package com.guardia.core.repository;

import com.guardia.core.model.EscenaNegativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Repositorio JPA para registros de escenas negativas.
 * Permite consultas por escena, por resultado y verificación de existencia.
 */
public interface EscenaNegativaRepository extends JpaRepository<EscenaNegativa, Long> {
    List<EscenaNegativa> findByEscenaId(Long escenaId);
    List<EscenaNegativa> findByResultado(String resultado);
    boolean existsByEscenaId(Long escenaId);
}
