package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.TipoDelitoRequest;
import com.guardia.core.dto.response.TipoDelitoResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.TipoDelitoService;
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
@DisplayName("TipoDelitoController - Pruebas Unitarias")
class TipoDelitoControllerTest {

    @Mock
    private TipoDelitoService tipoDelitoService;

    @InjectMocks
    private TipoDelitoController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private TipoDelitoResponse responseEjemplo() {
        return new TipoDelitoResponse(1L, "HOMICIDIO", "desc", true, List.of());
    }

    @Test
    @DisplayName("POST /api/v1/tipos-delito debe crear el tipo de delito y responder 201")
    void debeCrear() throws Exception {
        TipoDelitoRequest request = new TipoDelitoRequest("HOMICIDIO", "desc", true);
        when(tipoDelitoService.crear(any(TipoDelitoRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/tipos-delito")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre").value("HOMICIDIO"));
    }

    @Test
    @DisplayName("POST /api/v1/tipos-delito debe responder 400 cuando falta requiereSubtipo")
    void debeRechazarSinRequiereSubtipo() throws Exception {
        TipoDelitoRequest request = new TipoDelitoRequest("HOMICIDIO", "desc", null);

        mockMvc.perform(post("/api/v1/tipos-delito")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/tipos-delito/{id} debe retornar el tipo de delito")
    void debeObtenerPorId() throws Exception {
        when(tipoDelitoService.obtenerPorId(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/tipos-delito/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/tipos-delito debe retornar todos los tipos de delito")
    void debeObtenerTodos() throws Exception {
        when(tipoDelitoService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/tipos-delito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/tipos-delito/requieren-subtipo debe filtrar los que requieren subtipo")
    void debeObtenerQueRequierenSubtipo() throws Exception {
        when(tipoDelitoService.obtenerQueRequierenSubtipo()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/tipos-delito/requieren-subtipo"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/tipos-delito/{id} debe actualizar el tipo de delito")
    void debeActualizar() throws Exception {
        TipoDelitoRequest request = new TipoDelitoRequest("ROBO", "desc", false);
        when(tipoDelitoService.actualizar(eq(1L), any(TipoDelitoRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(put("/api/v1/tipos-delito/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/tipos-delito/{id} debe eliminar el tipo de delito")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/tipos-delito/1"))
                .andExpect(status().isOk());
    }
}
