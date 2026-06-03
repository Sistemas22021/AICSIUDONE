package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BulletImageRepository extends JpaRepository<BulletImagesEntity, UUID> {

    List<BulletImagesEntity> findByIdBullet(Long id);
    boolean existsByHashImage(String hashImage);
}
