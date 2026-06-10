package com.ccc.sistema_balistico.core;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.AuditLogViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuditSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BulletRepository bulletRepository;

    @Autowired
    private AuditLogViewRepository auditLogViewRepository;

    @Test
    @Transactional
    public void testAuditLoggingAndUnifiedView() throws Exception {
        // 1. Create and save a new Bullet
        BulletEntity bullet = BulletEntity.builder()
                .caseFile("EXP-TEST-999")
                .landsAndGrooves(6L)
                .manufacturer("Test Manufacturer")
                .createdAt(LocalDateTime.now())
                .isDelete(false)
                .build();

        BulletEntity savedBullet = bulletRepository.saveAndFlush(bullet);

        // 2. Fetch the audit logs from the unified view repository
        var auditLogs = auditLogViewRepository.findAll();
        
        // Assert that a log has been created for the bullet
        assertThat(auditLogs).isNotEmpty();
        boolean hasBulletAudit = auditLogs.stream()
                .anyMatch(log -> "BULLET".equals(log.getEntityType()) && log.getEntityId().equals(String.valueOf(savedBullet.getIdBullet())));
        assertThat(hasBulletAudit).isTrue();

        // 3. Test the REST endpoint with pagination
        mockMvc.perform(get("/api/v1/audit-log")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].operator").value("user"));
    }
}
