package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guardia.core.dto.request.EscenaRequest;
import com.guardia.core.dto.request.LiberarEscenaRequest;
import com.guardia.core.dto.response.EscenaResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.EscenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EscenaController - Pruebas Unitarias")
class EscenaControllerTest {

    @Mock
    private EscenaService escenaService;

    @InjectMocks
    private EscenaController escenaController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(escenaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private EscenaResponse escenaResponseEjemplo() {
        return new EscenaResponse(1L, "PENDIENTE", "ACTIVA", "ASEGURAMIENTO_PERIMETRO",
                null, null, 10L, null, List.of(), List.of(), null, null, null, null);
    }

    @Test
    @DisplayName("POST /api/v1/escenas debe crear la escena y responder 201")
    void debeCrearEscena() throws Exception {
        EscenaRequest request = new EscenaRequest(10L, UUID.randomUUID());
        when(escenaService.crear(any(EscenaRequest.class))).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(post("/api/v1/escenas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.estadoChecklist").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /api/v1/escenas/{id} debe retornar la escena solicitada")
    void debeObtenerPorId() throws Exception {
        when(escenaService.obtenerPorId(1L)).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(get("/api/v1/escenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/escenas debe retornar todas las escenas")
    void debeObtenerTodas() throws Exception {
        when(escenaService.obtenerTodos()).thenReturn(List.of(escenaResponseEjemplo()));

        mockMvc.perform(get("/api/v1/escenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/escenas/por-expediente/{id} debe filtrar por expediente")
    void debeObtenerPorExpediente() throws Exception {
        when(escenaService.obtenerPorExpediente(10L)).thenReturn(List.of(escenaResponseEjemplo()));

        mockMvc.perform(get("/api/v1/escenas/por-expediente/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].expedienteId").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/escenas/por-investigador/{id} debe filtrar por investigador")
    void debeObtenerPorInvestigador() throws Exception {
        UUID investigadorId = UUID.randomUUID();
        when(escenaService.obtenerPorInvestigador(investigadorId)).thenReturn(List.of(escenaResponseEjemplo()));

        mockMvc.perform(get("/api/v1/escenas/por-investigador/" + investigadorId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/escenas/{id} debe eliminar la escena")
    void debeEliminarEscena() throws Exception {
        mockMvc.perform(delete("/api/v1/escenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Escena eliminada."));
    }

    @Test
    @DisplayName("PATCH /api/v1/escenas/{id}/iniciar-checklist debe iniciar el checklist")
    void debeIniciarChecklist() throws Exception {
        when(escenaService.iniciarChecklist(1L)).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(patch("/api/v1/escenas/1/iniciar-checklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Checklist iniciado."));
    }

    @Test
    @DisplayName("PATCH /api/v1/escenas/{id}/cerrar debe cerrar la escena")
    void debeCerrarEscena() throws Exception {
        when(escenaService.cerrar(1L)).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(patch("/api/v1/escenas/1/cerrar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Escena cerrada."));
    }

    @Test
    @DisplayName("PATCH /api/v1/escenas/{id}/bloquear debe bloquear la edición")
    void debeBloquearEdicion() throws Exception {
        when(escenaService.bloquearEdicion(1L)).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(patch("/api/v1/escenas/1/bloquear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Edición bloqueada."));
    }

    @Test
    @DisplayName("GET /api/v1/escenas/{id}/validar-secuencia debe retornar el resultado de validación")
    void debeValidarSecuencia() throws Exception {
        when(escenaService.validarSecuencia(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/escenas/1/validar-secuencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/escenas/{id}/avanzar debe avanzar el paso del checklist")
    void debeAvanzarPaso() throws Exception {
        when(escenaService.avanzarPaso(1L)).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(patch("/api/v1/escenas/1/avanzar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paso completado."));
    }

    @Test
    @DisplayName("PATCH /api/v1/escenas/{id}/avanzar debe responder 422 cuando el checklist ya está completado")
    void debeResponder422CuandoChecklistCompletado() throws Exception {
        when(escenaService.avanzarPaso(1L)).thenThrow(new BusinessException("Checklist ya completado."));

        mockMvc.perform(patch("/api/v1/escenas/1/avanzar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Checklist ya completado."));
    }

    @Test
    @DisplayName("GET /api/v1/escenas/{id}/checklist debe retornar los pasos del checklist")
    void debeObtenerChecklist() throws Exception {
        when(escenaService.obtenerChecklist(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/escenas/1/checklist"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/escenas/{id}/liberar debe liberar formalmente la escena")
    void debeLiberarEscena() throws Exception {
        LiberarEscenaRequest request = new LiberarEscenaRequest(UUID.randomUUID(), "Todo en orden");
        when(escenaService.liberar(eq(1L), any(LiberarEscenaRequest.class))).thenReturn(escenaResponseEjemplo());

        mockMvc.perform(post("/api/v1/escenas/1/liberar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Escena liberada formalmente."));
    }

    @Test
    @DisplayName("POST /api/v1/escenas/{id}/liberar debe responder 400 cuando falta el investigador responsable")
    void debeRechazarLiberarSinInvestigador() throws Exception {
        LiberarEscenaRequest request = new LiberarEscenaRequest(null, "obs");

        mockMvc.perform(post("/api/v1/escenas/1/liberar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
