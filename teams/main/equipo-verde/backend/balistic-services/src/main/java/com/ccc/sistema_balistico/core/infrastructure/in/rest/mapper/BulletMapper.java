package com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;

public class BulletMapper {
    public static BulletDTO toDTO(BulletEntity bulletEntity){
        return BulletDTO.builder().
                idBullet(bulletEntity.getIdBullet()).
                caseFile(bulletEntity.getCaseFile()).
                landsAndGrooves(bulletEntity.getLandsAndGrooves()).
                percussionType(bulletEntity.getPercussionType()).
                twistDirection(bulletEntity.getTwistDirection()).
                caliber(bulletEntity.getCaliberEntity().getIdCaliber()).
                manufacturer(bulletEntity.getManufacturer()).
                createdAt(bulletEntity.getCreatedAt()).
                images(bulletEntity.getImagePaths().stream().map(BulletImagesEntity::getPathImage).toList()).
                build();


    }
    public static BulletEntity toEntity(BulletDTO bulletEntity, CaliberEntity caliberEntity){

        return BulletEntity.builder().
                idBullet(bulletEntity.getIdBullet()).
                caseFile(bulletEntity.getCaseFile()).
                landsAndGrooves(bulletEntity.getLandsAndGrooves()).
                percussionType(bulletEntity.getPercussionType()).
                twistDirection(bulletEntity.getTwistDirection()).
                caliberEntity(caliberEntity).
                manufacturer(bulletEntity.getManufacturer()).
                createdAt(bulletEntity.getCreatedAt()).isDelete(false).
                build();

    }

}
