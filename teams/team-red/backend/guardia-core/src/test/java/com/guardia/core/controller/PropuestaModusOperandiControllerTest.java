package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guardia.core.dto.request.AprobarPropuestaMoRequest;
import com.guardia.core.dto.request.CorregirPropuestaMoRequest;
import com.guardia.core.dto.request.RechazarPropuestaMoRequest;
import com.guardia.core.dto.response.PropuestaModusOperandiResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.service.DeteccionModusOperandiService;
import com.guardia.core.service.PropuestaModusOperandiService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropuestaModusOperandiController - Pruebas Unitarias")
class PropuestaModusOperandiControllerTest {

    @Mock private PropuestaModusOperandiService propuestaModusOperandiService;
    @Mock private DeteccionModusOperandiService deteccionModusOperandiService;

    @InjectMocks
    private PropuestaModusOperandiController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private PropuestaModusOperandiResponse responseEjemplo() {
        return new PropuestaModusOperandiResponse(50L, 1L, "EXP-2026-AAAA1111", 1, true,
                EstadoPropuestaMO.PENDIENTE, null, null, null, "resumen", 80.0,
                "modelo-emb", "modelo-chat", null, List.of(), false, null, null, null, null, null);
    }

    @Test
    @DisplayName("GET .../modus-operandi debe retornar la propuesta vigente del expediente")
    void debeObtenerVigente() throws Exception {
        when(propuestaModusOperandiService.obtenerVigentePorExpediente(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/expedientes/1/modus-operandi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.folioExpediente").value("EXP-2026-AAAA1111"));
    }

    @Test
    @DisplayName("GET .../modus-operandi/historial debe retornar el historial completo")
    void debeObtenerHistorial() throws Exception {
        when(propuestaModusOperandiService.historialPorExpediente(1L)).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/expedientes/1/modus-operandi/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("POST .../{propuestaId}/aprobar debe aprobar la propuesta")
    void debeAprobar() throws Exception {
        AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(UUID.randomUUID());
        when(propuestaModusOperandiService.aprobar(eq(50L), any(AprobarPropuestaMoRequest.class)))
                .thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/expedientes/1/modus-operandi/50/aprobar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Propuesta de MO aprobada."));
    }

    @Test
    @DisplayName("POST .../aprobar debe responder 400 cuando falta el analistaId")
    void debeRechazarAprobarSinAnalista() throws Exception {
        AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(null);

        mockMvc.perform(post("/api/v1/expedientes/1/modus-operandi/50/aprobar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST .../{propuestaId}/corregir debe corregir la propuesta")
    void debeCorregir() throws Exception {
        CorregirPropuestaMoRequest request = new CorregirPropuestaMoRequest(
                UUID.randomUUID(), "car", "firma", "zona", "Justificación obligatoria");
        when(propuestaModusOperandiService.corregir(eq(50L), any(CorregirPropuestaMoRequest.class)))
                .thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/expedientes/1/modus-operandi/50/corregir")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Propuesta de MO corregida."));
    }

    @Test
    @DisplayName("POST .../corregir debe responder 400 cuando falta la justificación")
    void debeRechazarCorregirSinJustificacion() throws Exception {
        CorregirPropuestaMoRequest request = new CorregirPropuestaMoRequest(
                UUID.randomUUID(), "car", null, null, "   ");

        mockMvc.perform(post("/api/v1/expedientes/1/modus-operandi/50/corregir")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST .../{propuestaId}/rechazar debe rechazar la propuesta")
    void debeRechazar() throws Exception {
        RechazarPropuestaMoRequest request = new RechazarPropuestaMoRequest(
                UUID.randomUUID(), "ROBO_SIMPLE", "No coincide el patrón");
        when(propuestaModusOperandiService.rechazar(eq(50L), any(RechazarPropuestaMoRequest.class)))
                .thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/expedientes/1/modus-operandi/50/rechazar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Propuesta de MO rechazada."));
    }

    @Test
    @DisplayName("POST .../modus-operandi/analizar debe encolar el análisis y responder 202")
    void debeAnalizarAhora() throws Exception {
        mockMvc.perform(post("/api/v1/expedientes/1/modus-operandi/analizar"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Análisis de Modus Operandi solicitado."));

        verify(deteccionModusOperandiService).analizarPatrones(1L);
    }
}
