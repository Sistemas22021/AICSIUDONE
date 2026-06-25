package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.application.dto.ImageDTO;
import com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper.BulletMapper;
import com.ccc.sistema_balistico.core.application.services.BulletImagesService;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletNotFound;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletImageRepository;
import com.ccc.sistema_balistico.core.application.services.FileStorageService;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.FileAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class BulletImageImpl implements BulletImagesService {

    @Autowired
    private BulletImageRepository bulletImageRepository;
    @Autowired
    private BulletRepository bulletRepository;
    @Autowired
    private FileStorageService fileStorageService;

    private String calculateSHA256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating SHA-256 hash: " + e.getMessage(), e);
        }
    }

    private BulletImagesEntity saveImage(MultipartFile file, BulletEntity bullet) {
        String fileHash = calculateSHA256(file);
        
        if (bulletImageRepository.existsByHashImage(fileHash)) {
            throw new FileAlreadyExistsException("This image already exists in the system.");
        }

        UUID imageUuid = UUID.randomUUID();
        String imagePath = fileStorageService.saveImageFile(file, imageUuid.toString());
        BulletImagesEntity bulletImages = BulletImagesEntity.builder().
                uuidBulletImages(imageUuid).
                idBullet(bullet).
                pathImage(imagePath).
                hashImage(fileHash).
                build();

        return bulletImageRepository.save(bulletImages);

    }

    @Transactional
    @Override
    public BulletDTO saveImageList(List<MultipartFile> file, Long idBullet) {
        BulletEntity bullet = bulletRepository.findById(idBullet).orElseThrow(() -> new BulletNotFound("Bullet Not Found for save image"));
        return saveImageList(file, bullet);

    }

    @Transactional
    @Override
    public BulletDTO saveImageList(List<MultipartFile> file, BulletEntity bulletEntity) {
        List<BulletImagesEntity> imagePath = file.stream().filter(img -> img != null && !img.isEmpty()).map(img -> this.saveImage(img, bulletEntity)).toList();
        bulletEntity.getImagePaths().addAll(imagePath);


        return BulletMapper.toDTO(bulletEntity);

    }

    @Override
    public ImageDTO loadImage(String img) {
        Resource imageFile = fileStorageService.loadImageFile(img);
        String contentType = fileStorageService.getContentType(img);
        return ImageDTO.builder().img(imageFile).contentType(contentType).build();
    }


    @Override
    public Void deleteImage(String path) {
        return null;
    }
}
