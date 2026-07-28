package com.ccc.sistema_balistico.core;

import com.ccc.sistema_balistico.core.application.dto.CorrelationResultDTO;
import com.ccc.sistema_balistico.core.application.services.CorrelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CorrelationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CorrelationService correlationService;

    @Test
    void testCorrelateBullet() throws Exception {
        CorrelationResultDTO result = new CorrelationResultDTO(2L, "EXP-TEST-002", "Manufacturer", 85.5, null);
        
        Page<CorrelationResultDTO> page = new PageImpl<>(Collections.singletonList(result));

        when(correlationService.correlateBullet(eq(1L), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(post("/api/v1/correlate/{evidenceId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
