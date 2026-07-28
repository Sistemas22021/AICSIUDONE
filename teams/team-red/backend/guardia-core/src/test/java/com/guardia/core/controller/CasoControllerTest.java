package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guardia.core.dto.request.CasoRequest;
import com.guardia.core.dto.response.CasoResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.service.CasoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración ligera (standalone MockMvc, sin contexto de Spring)
 * para {@link CasoController}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CasoController - Pruebas Unitarias")
class CasoControllerTest {

    @Mock
    private CasoService casoService;

    @InjectMocks
    private CasoController casoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(casoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private CasoResponse casoResponseEjemplo() {
        return new CasoResponse(1L, "CASO-2026-0001", "Mismo patrón", null, LocalDateTime.now(), null, List.of());
    }

    @Test
    @DisplayName("POST /api/v1/casos debe crear el caso y responder 201")
    void debeCrearCaso() throws Exception {
        CasoRequest request = new CasoRequest("aperez", List.of(10L, 20L), "Mismo patrón", null);
        when(casoService.crear(any(CasoRequest.class))).thenReturn(casoResponseEjemplo());

        mockMvc.perform(post("/api/v1/casos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.codigoCaso").value("CASO-2026-0001"));
    }

    @Test
    @DisplayName("POST /api/v1/casos debe responder 400 cuando el motivo está en blanco")
    void debeRechazarCasoSinMotivo() throws Exception {
        CasoRequest request = new CasoRequest("aperez", List.of(10L, 20L), "  ", null);

        mockMvc.perform(post("/api/v1/casos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/casos debe responder 400 cuando se envía un solo expediente")
    void debeRechazarCasoConUnSoloExpediente() throws Exception {
        CasoRequest request = new CasoRequest("aperez", List.of(10L), "Motivo válido", null);

        mockMvc.perform(post("/api/v1/casos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/casos/{id} debe retornar 200 cuando el caso existe")
    void debeObtenerCasoPorId() throws Exception {
        when(casoService.obtenerPorId(1L)).thenReturn(casoResponseEjemplo());

        mockMvc.perform(get("/api/v1/casos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/casos/{id} debe retornar 404 cuando el caso no existe")
    void debeRetornar404CuandoCasoNoExiste() throws Exception {
        when(casoService.obtenerPorId(99L)).thenThrow(new ResourceNotFoundException("Caso", 99L));

        mockMvc.perform(get("/api/v1/casos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Caso con id 99 no encontrado."));
    }

    @Test
    @DisplayName("GET /api/v1/casos debe retornar la lista completa de casos")
    void debeObtenerTodosLosCasos() throws Exception {
        when(casoService.obtenerTodos()).thenReturn(List.of(casoResponseEjemplo()));

        mockMvc.perform(get("/api/v1/casos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}
