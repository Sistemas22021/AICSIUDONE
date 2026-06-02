package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaliberRepository extends JpaRepository<CaliberEntity,Long> {

    List<CaliberEntity> findByIsDeleteFalse();
    Page<CaliberEntity> findByNameContainingIgnoreCaseAndIsDeleteFalse(String name, Pageable pageable);
}
