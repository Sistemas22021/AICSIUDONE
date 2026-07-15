package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper.BulletMapper;
import com.ccc.sistema_balistico.core.application.services.BulletService;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletNotFound;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.caliber.CaliberIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.caliber.CaliberNotFound;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.CaliberRepository;
import com.ccc.sistema_balistico.core.application.services.BulletImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BulletImpl implements BulletService {

    @Autowired
    BulletRepository bulletRepository;
    @Autowired
    CaliberRepository caliberRepository;
    @Autowired
    BulletImagesService bulletImagesService;
    @Autowired
    com.ccc.sistema_balistico.core.application.services.FileStorageService fileStorageService;


    @Autowired
    com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletImageRepository bulletImageRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<BulletDTO> getAll(Pageable pageable) {
        Page<BulletEntity> bulletEntityList = bulletRepository.findByIsDeleteFalse(pageable);
        return bulletEntityList.map(BulletMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public BulletDTO getBullet(Long idBullet) {
        BulletEntity bulletEntity = bulletRepository.findById(idBullet).orElseThrow(()-> new BulletNotFound("Bullet Not Found"));
        if (bulletEntity.getIsDelete()) throw new BulletIsDeleted();
        return BulletMapper.toDTO(bulletEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BulletDTO createBullet(BulletDTO bulletDTO, List<MultipartFile> files) {
        CaliberEntity caliber = caliberRepository.findById(bulletDTO.getCaliber()).orElseThrow(()-> new CaliberNotFound("Caliber Not Found"));
        if (caliber.getIsDelete()) throw new CaliberIsDeleted();
        bulletDTO.setIdBullet(null);
        bulletDTO.setCreatedAt(LocalDateTime.now());
        BulletEntity bulletEntity = BulletMapper.toEntity(bulletDTO,caliber);
        bulletEntity = bulletRepository.save(bulletEntity);

        return bulletImagesService.saveImageList(files,bulletEntity);
    }

    @Transactional
    @Override
    public BulletDTO updateBullet(Long idBullet, BulletDTO bulletDTO) {
        BulletEntity bulletEntity = bulletRepository.findById(idBullet).orElseThrow(()-> new BulletNotFound("Bullet Not Found"));
        bulletEntity.setManufacturer(bulletDTO.getManufacturer());
        bulletEntity.setLandsAndGrooves(bulletDTO.getLandsAndGrooves());
        bulletEntity.setPercussionType(bulletDTO.getPercussionType());
        bulletEntity.setTwistDirection(bulletDTO.getTwistDirection());

        return BulletMapper.toDTO(bulletRepository.save(bulletEntity));
    }

    @Transactional
    @Override
    public void deleteBullet(Long idBullet) {
        BulletEntity bulletEntity = bulletRepository.findById(idBullet).orElseThrow(()->{
            return new BulletNotFound("Bullet Not Found");
        });
        
        if (bulletEntity.getImagePaths() != null) {
            for (com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity img : bulletEntity.getImagePaths()) {
                fileStorageService.deleteImage(img.getPathImage());
            }
            bulletImageRepository.deleteAll(bulletEntity.getImagePaths());
            bulletEntity.getImagePaths().clear();
        }
        
        bulletEntity.setIsDelete(true);
        bulletRepository.save(bulletEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BulletDTO> searchBullets(String query, Pageable pageable) {
        String safeQuery = (query == null) ? "" : query.trim();
        Page<BulletEntity> page = bulletRepository.searchByQuery(safeQuery, pageable);
        return page.map(BulletMapper::toDTO);
    }
}
