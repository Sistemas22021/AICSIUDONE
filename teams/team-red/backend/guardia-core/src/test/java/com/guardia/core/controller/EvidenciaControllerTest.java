package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.EvidenciaRequest;
import com.guardia.core.dto.response.EvidenciaResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.EvidenciaService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvidenciaController - Pruebas Unitarias")
class EvidenciaControllerTest {

    @Mock
    private EvidenciaService evidenciaService;

    @InjectMocks
    private EvidenciaController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private EvidenciaResponse responseEjemplo() {
        return new EvidenciaResponse(1L, "EV-001", "ARMA", "Cuchillo", 5L, "hash123", null, "Carlos Ruiz");
    }

    @Test
    @DisplayName("POST /api/v1/evidencias debe registrar la evidencia y responder 201")
    void debeCrear() throws Exception {
        EvidenciaRequest request = new EvidenciaRequest(null, "ARMA", "Cuchillo", 5L, UUID.randomUUID(), null);
        when(evidenciaService.crear(any(EvidenciaRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/evidencias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.numeroItem").value("EV-001"));
    }

    @Test
    @DisplayName("POST /api/v1/evidencias debe responder 400 cuando falta el tipo")
    void debeRechazarSinTipo() throws Exception {
        EvidenciaRequest request = new EvidenciaRequest(null, "  ", "desc", 5L, null, null);

        mockMvc.perform(post("/api/v1/evidencias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/evidencias/{id} debe retornar la evidencia")
    void debeObtenerPorId() throws Exception {
        when(evidenciaService.obtenerPorId(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/evidencias/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/evidencias debe retornar todas las evidencias")
    void debeObtenerTodas() throws Exception {
        when(evidenciaService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/evidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/evidencias/por-escena/{id} debe filtrar por escena")
    void debeObtenerPorEscena() throws Exception {
        when(evidenciaService.obtenerPorEscena(5L)).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/evidencias/por-escena/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/evidencias/{id} debe actualizar la evidencia")
    void debeActualizar() throws Exception {
        EvidenciaRequest request = new EvidenciaRequest("EV-002", "HUELLA", "desc", 5L, null, null);
        when(evidenciaService.actualizar(eq(1L), any(EvidenciaRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(put("/api/v1/evidencias/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/evidencias/{id} debe delegar la eliminación en el servicio")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/evidencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evidencia eliminada."));
    }

    @Test
    @DisplayName("PATCH .../numero debe asignar el número recibido en el body")
    void debeAsignarNumero() throws Exception {
        when(evidenciaService.asignarNumero(1L, "EV-999")).thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/evidencias/1/numero")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("numero", "EV-999"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Número asignado."));
    }

    @Test
    @DisplayName("PATCH .../firmar/{investigadorId} debe firmar el levantamiento")
    void debeFirmarLevantamiento() throws Exception {
        UUID investigadorId = UUID.randomUUID();
        when(evidenciaService.firmarLevantamiento(1L, investigadorId)).thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/evidencias/1/firmar/" + investigadorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Levantamiento firmado."));
    }

    @Test
    @DisplayName("GET /api/v1/evidencias/{id}/validar-integridad debe retornar el resultado de validación")
    void debeValidarIntegridad() throws Exception {
        when(evidenciaService.validarIntegridad(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/evidencias/1/validar-integridad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/evidencias/{id}/verificar-hash debe responder con mensaje de éxito cuando el hash coincide")
    void debeVerificarHashExitoso() throws Exception {
        when(evidenciaService.verificarHash(1L)).thenReturn(true);

        mockMvc.perform(post("/api/v1/evidencias/1/verificar-hash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Integridad verificada: el hash coincide."))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/evidencias/{id}/verificar-hash debe responder con alerta cuando el hash no coincide")
    void debeVerificarHashConAlerta() throws Exception {
        when(evidenciaService.verificarHash(1L)).thenReturn(false);

        mockMvc.perform(post("/api/v1/evidencias/1/verificar-hash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ALERTA: discrepancia de integridad detectada."))
                .andExpect(jsonPath("$.data").value(false));
    }
}
