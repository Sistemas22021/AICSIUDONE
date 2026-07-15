package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.CorrelationResultDTO;
import com.ccc.sistema_balistico.core.application.dto.MatchResult;
import com.ccc.sistema_balistico.core.application.services.FileStorageService;
import com.ccc.sistema_balistico.core.application.services.ImageProcessingService;
import com.ccc.sistema_balistico.core.domain.enums.PercussionType;
import com.ccc.sistema_balistico.core.domain.enums.TwistDirection;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationServiceImplTest {

    @Mock
    private BulletRepository bulletRepository;

    @Mock
    private ImageProcessingService imageProcessingService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CorrelationServiceImpl correlationService;

    private BulletEntity sourceBullet;
    private List<BulletEntity> candidateList;

    @BeforeEach
    void setUp() {
        CaliberEntity caliber = CaliberEntity.builder().idCaliber(1L).name("9mm Parabellum").build();

        sourceBullet = BulletEntity.builder()
                .idBullet(1L)
                .caseFile("EXP-SOURCE")
                .caliberEntity(caliber)
                .twistDirection(TwistDirection.DEXTRORSUM)
                .percussionType(PercussionType.CENTRAL)
                .manufacturer("Winchester")
                .isDelete(false)
                .imagePaths(new ArrayList<>())
                .build();

        BulletImagesEntity srcImg = BulletImagesEntity.builder()
                .pathImage("uploads/src.png")
                .descriptor(new byte[]{1, 2, 3})
                .keypoints(new byte[]{4, 5, 6})
                .build();
        sourceBullet.getImagePaths().add(srcImg);

        candidateList = new ArrayList<>();

        BulletEntity c1 = BulletEntity.builder()
                .idBullet(2L)
                .caseFile("EXP-C1")
                .caliberEntity(caliber)
                .twistDirection(TwistDirection.DEXTRORSUM)
                .percussionType(PercussionType.CENTRAL)
                .manufacturer("Winchester")
                .isDelete(false)
                .imagePaths(new ArrayList<>())
                .build();
        
        BulletImagesEntity c1Img = BulletImagesEntity.builder()
                .pathImage("uploads/c1.png")
                .descriptor(new byte[]{7, 8, 9})
                .keypoints(new byte[]{10, 11, 12})
                .build();
        c1.getImagePaths().add(c1Img);
        candidateList.add(c1);

        BulletEntity c2 = BulletEntity.builder()
                .idBullet(3L)
                .caseFile("EXP-C2")
                .caliberEntity(caliber)
                .twistDirection(TwistDirection.SINISTRORSUM)
                .percussionType(PercussionType.CENTRAL)
                .manufacturer("Remington")
                .isDelete(false)
                .imagePaths(new ArrayList<>())
                .build();
        
        BulletImagesEntity c2Img = BulletImagesEntity.builder()
                .pathImage("uploads/c2.png")
                .descriptor(new byte[]{13, 14, 15})
                .keypoints(new byte[]{16, 17, 18})
                .build();
        c2.getImagePaths().add(c2Img);
        candidateList.add(c2);
    }

    @Test
    void testCorrelateBulletSuccess() {
        when(bulletRepository.findWithImagesByIdBulletAndIsDeleteFalse(1L)).thenReturn(Optional.of(sourceBullet));
        when(bulletRepository.findCompatibleCandidates(1L, 1L)).thenReturn(candidateList);

        when(imageProcessingService.matchFeatures(
                any(byte[].class), any(byte[].class), eq(null),
                eq(candidateList.get(0).getImagePaths().getFirst().getKeypoints()),
                eq(candidateList.get(0).getImagePaths().getFirst().getDescriptor()),
                eq(null)
        )).thenReturn(new MatchResult(10, null));

        // For C2: visual mismatch (inliers = 2, striaeMatched = false)
        when(imageProcessingService.matchFeatures(
                any(byte[].class), any(byte[].class), eq(null),
                eq(candidateList.get(1).getImagePaths().getFirst().getKeypoints()),
                eq(candidateList.get(1).getImagePaths().getFirst().getDescriptor()),
                eq(null)
        )).thenReturn(new MatchResult(2, null));

        when(fileStorageService.loadImageFile(anyString())).thenReturn(new ByteArrayResource(new byte[]{100}));
        
        when(imageProcessingService.matchFeatures(
                any(byte[].class), any(byte[].class), any(byte[].class),
                eq(candidateList.get(0).getImagePaths().getFirst().getKeypoints()),
                eq(candidateList.get(0).getImagePaths().getFirst().getDescriptor()),
                any(byte[].class)
        )).thenReturn(new MatchResult(10, "BASE64_IMAGE_STRING"));

        when(imageProcessingService.matchFeatures(
                any(byte[].class), any(byte[].class), any(byte[].class),
                eq(candidateList.get(1).getImagePaths().getFirst().getKeypoints()),
                eq(candidateList.get(1).getImagePaths().getFirst().getDescriptor()),
                any(byte[].class)
        )).thenReturn(new MatchResult(2, null));

        Pageable pageable = PageRequest.of(0, 10);
        Page<CorrelationResultDTO> result = correlationService.correlateBullet(1L, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        List<CorrelationResultDTO> content = result.getContent();
        assertEquals(2L, content.getFirst().idBullet());
        assertEquals(100.0, content.getFirst().matchScore());
        assertTrue(content.getFirst().breakdown().striaeMatched());
        assertTrue(content.get(0).breakdown().twistMatched());
        assertEquals("BASE64_IMAGE_STRING", content.get(0).breakdown().comparisonImageBase64());

        assertEquals(3L, content.get(1).idBullet());
        assertEquals(20.0, content.get(1).matchScore());
        assertFalse(content.get(1).breakdown().striaeMatched());
        assertFalse(content.get(1).breakdown().twistMatched());
    }
}
