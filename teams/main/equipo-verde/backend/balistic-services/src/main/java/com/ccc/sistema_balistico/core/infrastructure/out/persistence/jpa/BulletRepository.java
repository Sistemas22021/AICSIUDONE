package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BulletRepository extends JpaRepository<BulletEntity, Long> {

    Page<BulletEntity> findByIsDeleteFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"imagePaths"})
    Optional<BulletEntity> findWithImagesByIdBulletAndIsDeleteFalse(Long id);

    @Query("SELECT DISTINCT b FROM BulletEntity b LEFT JOIN FETCH b.imagePaths WHERE b.caliberEntity.idCaliber = :caliberId AND b.idBullet != :sourceId AND b.isDelete = false")
    List<BulletEntity> findCompatibleCandidates(
        @Param("caliberId") Long caliberId,
        @Param("sourceId") Long sourceId
    );
}
