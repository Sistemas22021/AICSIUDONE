package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BulletRepository extends JpaRepository<BulletEntity,Long> {

    Page<BulletEntity> findByIsDeleteFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"imagePaths"})
    Optional<BulletEntity> findWithImagesByIdBulletAndIsDeleteFalse(Long id);

}
