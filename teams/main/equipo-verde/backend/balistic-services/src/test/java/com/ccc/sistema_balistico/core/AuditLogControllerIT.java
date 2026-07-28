package com.ccc.sistema_balistico.core;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.AuditLogViewRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.CaliberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuditLogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BulletRepository bulletRepository;

    @Autowired
    private CaliberRepository caliberRepository;

    @Autowired
    private AuditLogViewRepository auditLogViewRepository;

    private CaliberEntity savedCaliber;

    @BeforeEach
    void setUp() {
        if (savedCaliber == null) {
            savedCaliber = new CaliberEntity();
            savedCaliber.setName("Audit Test Caliber");
            savedCaliber.setIsDelete(false);
            savedCaliber = caliberRepository.saveAndFlush(savedCaliber);
        }
    }

    @Test
    @Transactional
    void testAuditLogForCreateAndUpdateAndSoftDelete() throws Exception {
        // 1. Create a bullet
        BulletEntity bullet = new BulletEntity();
        bullet.setCaseFile("EXP-AUDIT-001");
        bullet.setManufacturer("Audit Manufacturer");
        bullet.setLandsAndGrooves(5L);
        bullet.setCreatedAt(LocalDateTime.now());
        bullet.setIsDelete(false);
        bullet.setCaliberEntity(savedCaliber);
        BulletEntity savedBullet = bulletRepository.saveAndFlush(bullet);

        // Commit transaction to trigger Envers listeners
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        // Check audit log for INSERT
        mockMvc.perform(get("/api/v1/audit-log")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.entityId == '" + savedBullet.getIdBullet() + "' && @.revType == 'ADD')]").exists());

        // 2. Update the bullet
        BulletDTO updateDto = BulletDTO.builder()
                .manufacturer("Updated Audit Manufacturer")
                .landsAndGrooves(6L)
                .build();

        mockMvc.perform(put("/api/v1/bullet/{id}", savedBullet.getIdBullet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        // Check audit log for UPDATE (MOD)
        mockMvc.perform(get("/api/v1/audit-log")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.entityId == '" + savedBullet.getIdBullet() + "' && @.revType == 'MOD')]").exists());

        // 3. Delete the bullet (Soft delete)
        mockMvc.perform(delete("/api/v1/bullet/{id}", savedBullet.getIdBullet()))
                .andExpect(status().isNoContent());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        // A soft delete is technically an UPDATE in the entity (is_delete=true), 
        // so it registers as a MOD in Envers.
        mockMvc.perform(get("/api/v1/audit-log")
                .param("page", "0")
                .param("size", "50")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.entityId == '" + savedBullet.getIdBullet() + "' && @.revType == 'MOD')]").exists());
                
        // Cleanup
        bulletRepository.deleteById(savedBullet.getIdBullet());
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }
}
