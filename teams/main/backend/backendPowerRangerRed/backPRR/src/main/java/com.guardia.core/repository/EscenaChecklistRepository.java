package com.guardia.core.repository;

import com.guardia.core.model.EscenaChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscenaChecklistRepository
        extends JpaRepository<EscenaChecklist, Long> {

    List<EscenaChecklist> findByEscenaIdOrderByOrden(Long escenaId);
}