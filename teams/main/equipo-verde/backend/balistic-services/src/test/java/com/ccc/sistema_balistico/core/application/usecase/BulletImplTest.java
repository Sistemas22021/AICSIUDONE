package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.application.services.BulletImagesService;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletNotFound;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.caliber.CaliberIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.caliber.CaliberNotFound;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.CaliberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulletImplTest {

    @Mock
    private BulletRepository bulletRepository;

    @Mock
    private CaliberRepository caliberRepository;

    @Mock
    private BulletImagesService bulletImagesService;

    @InjectMocks
    private BulletImpl bulletService;

    private BulletEntity sampleBulletEntity;
    private CaliberEntity sampleCaliberEntity;
    private BulletDTO sampleBulletDto;

    @BeforeEach
    void setUp() {
        sampleCaliberEntity = new CaliberEntity();
        sampleCaliberEntity.setIdCaliber(1L);
        sampleCaliberEntity.setName("9mm Parabellum");
        sampleCaliberEntity.setIsDelete(false);

        sampleBulletEntity = BulletEntity.builder()
                .idBullet(1L)
                .caseFile("EXP-2023-001")
                .caliberEntity(sampleCaliberEntity)
                .isDelete(false)
                .build();

        sampleBulletDto = BulletDTO.builder()
                .idBullet(1L)
                .caseFile("EXP-2023-001")
                .caliber(1L)
                .build();
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BulletEntity> page = new PageImpl<>(Collections.singletonList(sampleBulletEntity));
        when(bulletRepository.findByIsDeleteFalse(pageable)).thenReturn(page);

        Page<BulletDTO> result = bulletService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("EXP-2023-001", result.getContent().getFirst().getCaseFile());
        verify(bulletRepository, times(1)).findByIsDeleteFalse(pageable);
    }

    @Test
    void testGetBulletSuccess() {
        when(bulletRepository.findById(1L)).thenReturn(Optional.of(sampleBulletEntity));

        BulletDTO result = bulletService.getBullet(1L);

        assertNotNull(result);
        assertEquals("EXP-2023-001", result.getCaseFile());
        assertEquals(1L, result.getCaliber());
    }

    @Test
    void testGetBulletNotFound() {
        when(bulletRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BulletNotFound.class, () -> bulletService.getBullet(1L));
    }

    @Test
    void testGetBulletDeleted() {
        sampleBulletEntity.setIsDelete(true);
        when(bulletRepository.findById(1L)).thenReturn(Optional.of(sampleBulletEntity));

        assertThrows(BulletIsDeleted.class, () -> bulletService.getBullet(1L));
    }

    @Test
    void testCreateBulletSuccess() {
        BulletDTO inputDto = BulletDTO.builder()
                .caseFile("EXP-2023-001")
                .caliber(1L)
                .build();

        when(caliberRepository.findById(1L)).thenReturn(Optional.of(sampleCaliberEntity));
        when(bulletRepository.save(any(BulletEntity.class))).thenReturn(sampleBulletEntity);
        when(bulletImagesService.saveImageList(anyList(), any(BulletEntity.class))).thenReturn(sampleBulletDto);

        BulletDTO result = bulletService.createBullet(inputDto, Collections.emptyList());

        assertNotNull(result);
        verify(caliberRepository, times(1)).findById(1L);
        verify(bulletRepository, times(1)).save(any(BulletEntity.class));
        verify(bulletImagesService, times(1)).saveImageList(anyList(), any(BulletEntity.class));
    }

    @Test
    void testCreateBulletCaliberNotFound() {
        BulletDTO inputDto = BulletDTO.builder()
                .caseFile("EXP-2023-001")
                .caliber(1L)
                .build();

        when(caliberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CaliberNotFound.class, () -> bulletService.createBullet(inputDto, Collections.emptyList()));
    }

    @Test
    void testCreateBulletCaliberDeleted() {
        BulletDTO inputDto = BulletDTO.builder()
                .caseFile("EXP-2023-001")
                .caliber(1L)
                .build();
        sampleCaliberEntity.setIsDelete(true);

        when(caliberRepository.findById(1L)).thenReturn(Optional.of(sampleCaliberEntity));

        assertThrows(CaliberIsDeleted.class, () -> bulletService.createBullet(inputDto, Collections.emptyList()));
    }

    @Test
    void testUpdateBulletSuccess() {
        BulletDTO inputDto = BulletDTO.builder()
                .manufacturer("Federal")
                .landsAndGrooves(6L)
                .build();

        when(bulletRepository.findById(1L)).thenReturn(Optional.of(sampleBulletEntity));
        when(bulletRepository.save(any(BulletEntity.class))).thenReturn(sampleBulletEntity);

        BulletDTO result = bulletService.updateBullet(1L, inputDto);

        assertNotNull(result);
        assertEquals("Federal", sampleBulletEntity.getManufacturer());
        assertEquals(6L, sampleBulletEntity.getLandsAndGrooves());
        verify(bulletRepository, times(1)).save(sampleBulletEntity);
    }

    @Test
    void testDeleteBulletSuccess() {
        when(bulletRepository.findById(1L)).thenReturn(Optional.of(sampleBulletEntity));
        when(bulletRepository.save(any(BulletEntity.class))).thenReturn(sampleBulletEntity);

        bulletService.deleteBullet(1L);

        assertTrue(sampleBulletEntity.getIsDelete());
        verify(bulletRepository, times(1)).save(sampleBulletEntity);
    }
}
