package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.SubtipoDelitoRequest;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.SubtipoDelitoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubtipoDelitoController - Pruebas Unitarias")
class SubtipoDelitoControllerTest {

    @Mock
    private SubtipoDelitoService subtipoDelitoService;

    @InjectMocks
    private SubtipoDelitoController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private SubtipoDelitoResponse responseEjemplo() {
        return new SubtipoDelitoResponse(10L, "HOMICIDIO_CULPOSO", "desc", 1L, "HOMICIDIO");
    }

    @Test
    @DisplayName("POST /api/v1/subtipos-delito debe crear el subtipo y responder 201")
    void debeCrear() throws Exception {
        SubtipoDelitoRequest request = new SubtipoDelitoRequest("HOMICIDIO_CULPOSO", "desc", 1L);
        when(subtipoDelitoService.crear(any(SubtipoDelitoRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/subtipos-delito")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre").value("HOMICIDIO_CULPOSO"));
    }

    @Test
    @DisplayName("POST /api/v1/subtipos-delito debe responder 400 cuando falta el tipoDelitoId")
    void debeRechazarSinTipoDelitoId() throws Exception {
        SubtipoDelitoRequest request = new SubtipoDelitoRequest("HOMICIDIO_CULPOSO", "desc", null);

        mockMvc.perform(post("/api/v1/subtipos-delito")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/subtipos-delito/{id} debe retornar el subtipo")
    void debeObtenerPorId() throws Exception {
        when(subtipoDelitoService.obtenerPorId(10L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/subtipos-delito/10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/subtipos-delito debe retornar todos los subtipos")
    void debeObtenerTodos() throws Exception {
        when(subtipoDelitoService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/subtipos-delito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/subtipos-delito/por-tipo/{id} debe filtrar por tipo padre")
    void debeObtenerPorTipo() throws Exception {
        when(subtipoDelitoService.obtenerPorTipoDelito(1L)).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/subtipos-delito/por-tipo/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/subtipos-delito/{id} debe actualizar el subtipo")
    void debeActualizar() throws Exception {
        SubtipoDelitoRequest request = new SubtipoDelitoRequest("NUEVO", "desc", 1L);
        when(subtipoDelitoService.actualizar(eq(10L), any(SubtipoDelitoRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(put("/api/v1/subtipos-delito/10")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/subtipos-delito/{id} debe eliminar el subtipo")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/subtipos-delito/10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET .../{subtipoId}/validar-tipo/{tipoId} debe retornar el resultado de la validación")
    void debeValidarCorrespondencia() throws Exception {
        when(subtipoDelitoService.validarCorrespondencia(10L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/subtipos-delito/10/validar-tipo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}
