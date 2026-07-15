package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.FileAlreadyExistsException;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletImageRepository;
import com.ccc.sistema_balistico.core.application.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulletImageImplTest {

    @Mock
    private BulletImageRepository bulletImageRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private BulletImageImpl bulletImageService;

    private BulletEntity sampleBullet;
    private MockMultipartFile sampleFile;
    private String expectedHash;

    @BeforeEach
    void setUp() throws Exception {
        sampleBullet = BulletEntity.builder()
                .idBullet(1L)
                .caseFile("EXP-2023-001")
                .imagePaths(new java.util.ArrayList<>())
                .build();

        sampleFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "some test image bytes".getBytes()
        );

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(sampleFile.getBytes());
        expectedHash = HexFormat.of().formatHex(hashBytes);
    }

    @Test
    void testSaveImageListSuccess() {
        when(bulletImageRepository.existsByHashImage(expectedHash)).thenReturn(false);
        when(fileStorageService.saveImageFile(any(), anyString())).thenReturn("image-path.png");
        when(bulletImageRepository.save(any(BulletImagesEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = bulletImageService.saveImageList(Collections.singletonList(sampleFile), sampleBullet);

        assertNotNull(result);
        verify(bulletImageRepository, times(1)).existsByHashImage(expectedHash);
        verify(fileStorageService, times(1)).saveImageFile(eq(sampleFile), anyString());
        verify(bulletImageRepository, times(1)).save(any(BulletImagesEntity.class));
    }

    @Test
    void testSaveImageListDuplicateThrowsException() {
        when(bulletImageRepository.existsByHashImage(expectedHash)).thenReturn(true);

        assertThrows(FileAlreadyExistsException.class, () -> 
                bulletImageService.saveImageList(Collections.singletonList(sampleFile), sampleBullet));

        verify(bulletImageRepository, times(1)).existsByHashImage(expectedHash);
        verify(fileStorageService, never()).saveImageFile(any(), anyString());
        verify(bulletImageRepository, never()).save(any(BulletImagesEntity.class));
    }
}
