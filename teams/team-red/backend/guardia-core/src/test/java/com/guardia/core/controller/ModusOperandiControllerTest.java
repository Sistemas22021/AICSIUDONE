package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.ModusOperandiRequest;
import com.guardia.core.dto.response.ModusOperandiResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.ModusOperandiService;
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
@DisplayName("ModusOperandiController - Pruebas Unitarias")
class ModusOperandiControllerTest {

    @Mock
    private ModusOperandiService modusOperandiService;

    @InjectMocks
    private ModusOperandiController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ModusOperandiResponse responseEjemplo() {
        return new ModusOperandiResponse(1L, "Robo nocturno", "ROBO_NOCTURNO", "ALTO", List.of(10L));
    }

    @Test
    @DisplayName("POST /api/v1/modus-operandi debe crear el registro y responder 201")
    void debeCrear() throws Exception {
        ModusOperandiRequest request = new ModusOperandiRequest("Robo nocturno", "ROBO_NOCTURNO", "ALTO");
        when(modusOperandiService.crear(any(ModusOperandiRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/modus-operandi")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.patronDetectado").value("ROBO_NOCTURNO"));
    }

    @Test
    @DisplayName("POST /api/v1/modus-operandi debe responder 400 cuando falta la descripción analítica")
    void debeRechazarSinDescripcion() throws Exception {
        ModusOperandiRequest request = new ModusOperandiRequest("   ", null, null);

        mockMvc.perform(post("/api/v1/modus-operandi")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/modus-operandi/{id} debe retornar el registro")
    void debeObtenerPorId() throws Exception {
        when(modusOperandiService.obtenerPorId(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/modus-operandi/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/modus-operandi debe retornar todos los registros")
    void debeObtenerTodos() throws Exception {
        when(modusOperandiService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/modus-operandi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/modus-operandi/buscar debe delegar el patrón como query param")
    void debeBuscarPorPatron() throws Exception {
        when(modusOperandiService.buscarPorPatron("robo")).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/modus-operandi/buscar").param("patron", "robo"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/modus-operandi/{id} debe actualizar el registro")
    void debeActualizar() throws Exception {
        ModusOperandiRequest request = new ModusOperandiRequest("Nueva desc", "PATRON_X", "MEDIO");
        when(modusOperandiService.actualizar(eq(1L), any(ModusOperandiRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(put("/api/v1/modus-operandi/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/modus-operandi/{id} debe eliminar el registro")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/modus-operandi/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST .../{modusId}/expedientes/{expedienteId} debe vincular el expediente")
    void debeVincularExpediente() throws Exception {
        when(modusOperandiService.vincularExpediente(1L, 10L)).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/modus-operandi/1/expedientes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expediente vinculado."));
    }

    @Test
    @DisplayName("DELETE .../{modusId}/expedientes/{expedienteId} debe desvincular el expediente")
    void debeDesvincularExpediente() throws Exception {
        when(modusOperandiService.desvincularExpediente(1L, 10L)).thenReturn(responseEjemplo());

        mockMvc.perform(delete("/api/v1/modus-operandi/1/expedientes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expediente desvinculado."));
    }

    @Test
    @DisplayName("PATCH .../{id}/patron debe actualizar el patrón desde el body")
    void debeAgregarPatron() throws Exception {
        when(modusOperandiService.agregarPatron(1L, "NUEVO_PATRON")).thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/modus-operandi/1/patron")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("patron", "NUEVO_PATRON"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET .../{modusId}/comparar debe retornar el porcentaje de similitud")
    void debeCompararExpedientes() throws Exception {
        when(modusOperandiService.compararExpedientes(1L, 10L, 20L)).thenReturn(80.0);

        mockMvc.perform(get("/api/v1/modus-operandi/1/comparar")
                        .param("expedienteAId", "10").param("expedienteBId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(80.0));
    }
}
