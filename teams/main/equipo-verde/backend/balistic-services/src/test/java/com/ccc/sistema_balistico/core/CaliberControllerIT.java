package com.ccc.sistema_balistico.core;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.CaliberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CaliberControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaliberRepository caliberRepository;

    @BeforeEach
    void setUp() {
        CaliberEntity entity = new CaliberEntity();
        entity.setName("TEST-9MM");
        entity.setIsDelete(false);
        caliberRepository.saveAndFlush(entity);
    }

    @Test
    void testSearchCalibers() throws Exception {
        mockMvc.perform(get("/api/v1/caliber/search")
                .param("query", "TEST")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("TEST-9MM"));
    }

    @Test
    void testSearchCalibersEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/caliber/search")
                .param("query", "NON_EXISTENT_CALIBER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
