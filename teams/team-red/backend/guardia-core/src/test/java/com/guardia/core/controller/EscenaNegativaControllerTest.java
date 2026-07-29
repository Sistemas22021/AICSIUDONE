package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.EscenaNegativaRequest;
import com.guardia.core.dto.response.EscenaNegativaResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.EscenaNegativaService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EscenaNegativaController - Pruebas Unitarias")
class EscenaNegativaControllerTest {

    @Mock
    private EscenaNegativaService escenaNegativaService;

    @InjectMocks
    private EscenaNegativaController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private EscenaNegativaResponse responseEjemplo() {
        return new EscenaNegativaResponse(1L, "Arma", "Cocina", "NO_ENCONTRADO", "obs", 5L, false);
    }

    @Test
    @DisplayName("POST /api/v1/escenas-negativas debe crear el registro y responder 201")
    void debeCrear() throws Exception {
        EscenaNegativaRequest request = new EscenaNegativaRequest("Arma", "Cocina", null, null, 5L, null);
        when(escenaNegativaService.crear(any(EscenaNegativaRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/escenas-negativas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.elementoBuscado").value("Arma"));
    }

    @Test
    @DisplayName("POST /api/v1/escenas-negativas debe responder 400 cuando falta el escenaId")
    void debeRechazarSinEscenaId() throws Exception {
        EscenaNegativaRequest request = new EscenaNegativaRequest("Arma", "Cocina", null, null, null, null);

        mockMvc.perform(post("/api/v1/escenas-negativas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/escenas-negativas/{id} debe retornar el registro")
    void debeObtenerPorId() throws Exception {
        when(escenaNegativaService.obtenerPorId(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/escenas-negativas/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/escenas-negativas debe retornar todos los registros")
    void debeObtenerTodos() throws Exception {
        when(escenaNegativaService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/escenas-negativas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/escenas-negativas/por-escena/{id} debe filtrar por escena")
    void debeObtenerPorEscena() throws Exception {
        when(escenaNegativaService.obtenerPorEscena(5L)).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/escenas-negativas/por-escena/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/escenas-negativas/{id} debe actualizar el registro")
    void debeActualizar() throws Exception {
        EscenaNegativaRequest request = new EscenaNegativaRequest("Arma", "Baño", null, null, 5L, null);
        when(escenaNegativaService.actualizar(eq(1L), any(EscenaNegativaRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(put("/api/v1/escenas-negativas/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Escena negativa actualizada."));
    }

    @Test
    @DisplayName("DELETE /api/v1/escenas-negativas/{id} debe delegar la eliminación en el servicio")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/escenas-negativas/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH .../resultado-no-encontrado debe registrar área y observación desde el body")
    void debeRegistrarResultadoNoEncontrado() throws Exception {
        when(escenaNegativaService.registrarResultadoNoEncontrado(1L, "Cocina", "obs")).thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/escenas-negativas/1/resultado-no-encontrado")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("area", "Cocina", "observacion", "obs"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resultado registrado."));
    }

    @Test
    @DisplayName("PATCH .../observacion debe agregar la observación del body")
    void debeAgregarObservacion() throws Exception {
        when(escenaNegativaService.agregarObservacion(1L, "Nueva obs")).thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/escenas-negativas/1/observacion")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("observacion", "Nueva obs"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/escenas-negativas/{id}/validar debe retornar el resultado de validación")
    void debeValidarRegistro() throws Exception {
        when(escenaNegativaService.validarRegistro(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/escenas-negativas/1/validar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}
