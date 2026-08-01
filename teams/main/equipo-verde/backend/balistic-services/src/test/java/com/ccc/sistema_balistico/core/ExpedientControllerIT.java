package com.ccc.sistema_balistico.core;

import com.ccc.sistema_balistico.core.application.dto.ExpedientDTO;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.ExpedientEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.ExpedientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ExpedientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpedientRepository expedientRepository;

    private ExpedientEntity savedExpedient;

    @BeforeEach
    void setUp() {
        ExpedientEntity entity = new ExpedientEntity();
        entity.setCaseNumber("EXP-TEST-001");
        entity.setDescription("Test Expedient");
        entity.setStatus("ABIERTO");
        entity.setIsDelete(false);
        savedExpedient = expedientRepository.saveAndFlush(entity);
    }

    @Test
    void testCreateExpedient() throws Exception {
        ExpedientDTO newExpedient = ExpedientDTO.builder()
                .caseNumber("EXP-TEST-002")
                .description("New Description")
                .status("ABIERTO")
                .build();

        mockMvc.perform(post("/api/v1/expedients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newExpedient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseNumber").value("EXP-TEST-002"))
                .andExpect(jsonPath("$.idExpedient").exists());
    }

    @Test
    void testGetExpedientById() throws Exception {
        mockMvc.perform(get("/api/v1/expedients/{id}", savedExpedient.getIdExpedient())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseNumber").value("EXP-TEST-001"));
    }

    @Test
    void testGetExpedientByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/expedients/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllExpedients() throws Exception {
        mockMvc.perform(get("/api/v1/expedients")
                .param("keyword", "EXP-TEST")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].caseNumber").value("EXP-TEST-001"));
    }

    @Test
    void testUpdateExpedient() throws Exception {
        ExpedientDTO updateDto = ExpedientDTO.builder()
                .caseNumber("EXP-TEST-001-UPDATED")
                .description("Updated Description")
                .status("CERRADO")
                .build();

        mockMvc.perform(put("/api/v1/expedients/{id}", savedExpedient.getIdExpedient())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseNumber").value("EXP-TEST-001-UPDATED"))
                .andExpect(jsonPath("$.status").value("CERRADO"));

        ExpedientEntity updated = expedientRepository.findById(savedExpedient.getIdExpedient()).orElseThrow();
        assertThat(updated.getCaseNumber()).isEqualTo("EXP-TEST-001-UPDATED");
    }

    @Test
    void testDeleteExpedient() throws Exception {
        mockMvc.perform(delete("/api/v1/expedients/{id}", savedExpedient.getIdExpedient()))
                .andExpect(status().isNoContent());

        ExpedientEntity deleted = expedientRepository.findById(savedExpedient.getIdExpedient()).orElseThrow();
        assertThat(deleted.getIsDelete()).isTrue();
    }
}
