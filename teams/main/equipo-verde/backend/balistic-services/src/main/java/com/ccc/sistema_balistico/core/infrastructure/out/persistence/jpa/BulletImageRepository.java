package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BulletImageRepository extends JpaRepository<BulletImagesEntity, UUID> {

    @Query("SELECT bi FROM BulletImagesEntity bi WHERE bi.idBullet.idBullet = :id")
    List<BulletImagesEntity> findByIdBullet(@Param("id") Long id);
    boolean existsByHashImage(String hashImage);
}
