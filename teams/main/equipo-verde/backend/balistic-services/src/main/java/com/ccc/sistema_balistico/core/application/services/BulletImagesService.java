package com.ccc.sistema_balistico.core.application.services;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.application.dto.ImageDTO;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BulletImagesService {
    BulletDTO saveImageList(List<MultipartFile> file, Long idBullet);
    BulletDTO saveImageList(List<MultipartFile> file, BulletEntity bulletEntity);
    ImageDTO loadImage(String img);
    Void deleteImage(String path);
}
