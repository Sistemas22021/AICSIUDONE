package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.ExpedientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExpedientRepository extends JpaRepository<ExpedientEntity, Long> {

    @Query("SELECT e FROM ExpedientEntity e WHERE e.idExpedient = :idExpedient AND e.isDelete = false")
    Optional<ExpedientEntity> findByIdAndIsDeleteFalse(@Param("idExpedient") Long idExpedient);

    @Query("SELECT e FROM ExpedientEntity e WHERE e.isDelete = false AND " +
           "(UPPER(e.caseNumber) LIKE UPPER(CONCAT('%', :keyword, '%')) OR " +
           "UPPER(e.description) LIKE UPPER(CONCAT('%', :keyword, '%')))")
    Page<ExpedientEntity> findAllByIsDeleteFalseAndKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT e FROM ExpedientEntity e WHERE e.isDelete = false")
    Page<ExpedientEntity> findAllByIsDeleteFalse(Pageable pageable);
}
