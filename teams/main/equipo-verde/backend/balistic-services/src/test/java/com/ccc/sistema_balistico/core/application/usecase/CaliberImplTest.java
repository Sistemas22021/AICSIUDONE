package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.CaliberDTO;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaliberImplTest {

    @Mock
    private CaliberRepository caliberRepository;

    @InjectMocks
    private CaliberImpl caliberService;

    private CaliberEntity sampleCaliberEntity;

    @BeforeEach
    void setUp() {
        sampleCaliberEntity = new CaliberEntity();
        sampleCaliberEntity.setIdCaliber(1L);
        sampleCaliberEntity.setName("9mm Parabellum");
        sampleCaliberEntity.setIsDelete(false);
    }

    @Test
    void testSearchCalibersSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CaliberEntity> page = new PageImpl<>(Collections.singletonList(sampleCaliberEntity));
        
        when(caliberRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(eq("9mm"), any(PageRequest.class)))
                .thenReturn(page);

        Page<CaliberDTO> result = caliberService.searchCalibers("9mm");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("9mm Parabellum", result.getContent().getFirst().getName());
        assertEquals(1L, result.getContent().getFirst().getIdCaliber());
        verify(caliberRepository, times(1))
                .findByNameContainingIgnoreCaseAndIsDeleteFalse(eq("9mm"), eq(pageRequest));
    }

    @Test
    void testSearchCalibersEmptyQuery() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CaliberEntity> page = new PageImpl<>(Collections.emptyList());

        when(caliberRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(eq(""), any(PageRequest.class)))
                .thenReturn(page);

        Page<CaliberDTO> result = caliberService.searchCalibers(null);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(caliberRepository, times(1))
                .findByNameContainingIgnoreCaseAndIsDeleteFalse(eq(""), eq(pageRequest));
    }
}
