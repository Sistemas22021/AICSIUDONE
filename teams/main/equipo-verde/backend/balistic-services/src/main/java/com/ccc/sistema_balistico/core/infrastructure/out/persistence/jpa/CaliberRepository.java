package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CaliberRepository extends JpaRepository<CaliberEntity, Long> {

    List<CaliberEntity> findByIsDeleteFalse();

    @Query("SELECT c FROM CaliberEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND (c.isDelete = false OR c.isDelete IS NULL)")
    Page<CaliberEntity> findByNameContainingIgnoreCaseAndIsDeleteFalse(@Param("name") String name, Pageable pageable);
}
