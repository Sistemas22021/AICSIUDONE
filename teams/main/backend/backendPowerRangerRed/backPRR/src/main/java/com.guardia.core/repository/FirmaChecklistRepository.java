package com.guardia.core.repository;

import com.guardia.core.model.FirmaChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface FirmaChecklistRepository extends JpaRepository<FirmaChecklist, Long> {

    List<FirmaChecklist> findByPasoChecklistEscenaIdOrderByTimestampFirmaAsc(Long escenaId);

    List<FirmaChecklist> findByPasoChecklistIdAndExitosoTrue(Long pasoChecklistId);
}