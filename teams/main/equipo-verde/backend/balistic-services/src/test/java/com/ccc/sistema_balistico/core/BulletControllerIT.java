package com.ccc.sistema_balistico.core;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.application.dto.ImageDTO;
import com.ccc.sistema_balistico.core.application.services.BulletImagesService;
import com.ccc.sistema_balistico.core.domain.enums.BulletStatus;
import com.ccc.sistema_balistico.core.domain.enums.PercussionType;
import com.ccc.sistema_balistico.core.domain.enums.TwistDirection;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.CaliberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BulletControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BulletRepository bulletRepository;

    @Autowired
    private CaliberRepository caliberRepository;

    @MockitoBean
    private BulletImagesService bulletImagesService;

    private BulletEntity savedBullet;
    private CaliberEntity savedCaliber;

    @BeforeEach
    void setUp() {
        savedCaliber = new CaliberEntity();
        savedCaliber.setName("9mm Test");
        savedCaliber.setIsDelete(false);
        savedCaliber = caliberRepository.saveAndFlush(savedCaliber);

        BulletEntity bullet = new BulletEntity();
        bullet.setCaseFile("EXP-TEST-001");
        bullet.setManufacturer("Test Manufacturer");
        bullet.setLandsAndGrooves(6L);
        bullet.setTwistDirection(TwistDirection.DEXTRORSUM);
        bullet.setPercussionType(PercussionType.CENTRAL);
        bullet.setStatus(BulletStatus.EN_INVESTIGACION);
        bullet.setIsDelete(false);
        bullet.setCaliberEntity(savedCaliber);
        savedBullet = bulletRepository.saveAndFlush(bullet);
    }

    @Test
    void testGetAllBullets() throws Exception {
        mockMvc.perform(get("/api/v1/bullet")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].caseFile").value("EXP-TEST-001"));
    }

    @Test
    void testGetBulletById() throws Exception {
        mockMvc.perform(get("/api/v1/bullet/{id}", savedBullet.getIdBullet())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseFile").value("EXP-TEST-001"));
    }

    @Test
    void testCreateBullet() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        BulletDTO mockResponse = BulletDTO.builder()
                .idBullet(999L)
                .caseFile("EXP-TEST-002")
                .caliber(savedCaliber.getIdCaliber())
                .build();

        when(bulletImagesService.saveImageList(any(List.class), any(BulletEntity.class))).thenReturn(mockResponse);

        mockMvc.perform(multipart("/api/v1/bullet")
                .file(file)
                .param("caseFile", "EXP-TEST-002")
                .param("caliber", savedCaliber.getIdCaliber().toString())
                .param("manufacturer", "Federal")
                .param("landsAndGrooves", "5")
                .param("twistDirection", "SINISTRORSUM")
                .param("percussionType", "CENTRAL")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseFile").value("EXP-TEST-002"));
    }

    @Test
    void testUpdateBullet() throws Exception {
        BulletDTO updateDto = BulletDTO.builder()
                .manufacturer("Updated Manufacturer")
                .landsAndGrooves(7L)
                .build();

        mockMvc.perform(put("/api/v1/bullet/{id}", savedBullet.getIdBullet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manufacturer").value("Updated Manufacturer"))
                .andExpect(jsonPath("$.landsAndGrooves").value(7));

        BulletEntity updated = bulletRepository.findById(savedBullet.getIdBullet()).orElseThrow();
        assertThat(updated.getManufacturer()).isEqualTo("Updated Manufacturer");
    }

    @Test
    void testDeleteBullet() throws Exception {
        mockMvc.perform(delete("/api/v1/bullet/{id}", savedBullet.getIdBullet()))
                .andExpect(status().isNoContent());

        BulletEntity deleted = bulletRepository.findById(savedBullet.getIdBullet()).orElseThrow();
        assertThat(deleted.getIsDelete()).isTrue();
        assertThat(deleted.getStatus()).isEqualTo(BulletStatus.ARCHIVADO);
    }
}
