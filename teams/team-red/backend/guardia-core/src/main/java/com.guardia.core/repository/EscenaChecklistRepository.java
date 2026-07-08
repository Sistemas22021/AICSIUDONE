package com.guardia.core.repository;

import com.guardia.core.model.EscenaChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Repositorio JPA para los pasos del checklist de una escena.
 * Provee métodos para listar los pasos ordenados por su orden dentro de la escena.
 */
public interface EscenaChecklistRepository
        extends JpaRepository<EscenaChecklist, Long> {

    List<EscenaChecklist> findByEscenaIdOrderByOrden(Long escenaId);
}