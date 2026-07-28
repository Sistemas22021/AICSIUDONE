package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.LocalizacionRequest;
import com.guardia.core.dto.response.LocalizacionResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.service.LocalizacionService;
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
@DisplayName("LocalizacionController - Pruebas Unitarias")
class LocalizacionControllerTest {

    @Mock
    private LocalizacionService localizacionService;

    @InjectMocks
    private LocalizacionController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private LocalizacionResponse responseEjemplo() {
        return new LocalizacionResponse(1L, "Libertador", "Catia", "Av. Principal",
                "Cerca de la plaza", 10.5, -66.9, "Libertador, Catia - Av. Principal");
    }

    @Test
    @DisplayName("POST /api/v1/localizaciones debe crear la localización y responder 201")
    void debeCrear() throws Exception {
        LocalizacionRequest request = new LocalizacionRequest("Libertador", "Catia", "Av. Principal", "ref", 10.5, -66.9);
        when(localizacionService.crear(any(LocalizacionRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(post("/api/v1/localizaciones")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.municipio").value("Libertador"));
    }

    @Test
    @DisplayName("GET /api/v1/localizaciones/{id} debe retornar la localización")
    void debeObtenerPorId() throws Exception {
        when(localizacionService.obtenerPorId(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/localizaciones/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/localizaciones debe retornar todas las localizaciones")
    void debeObtenerTodas() throws Exception {
        when(localizacionService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/localizaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/localizaciones/{id} debe actualizar la localización")
    void debeActualizar() throws Exception {
        LocalizacionRequest request = new LocalizacionRequest("Chacao", "Altamira", "dir", "ref", 1.0, 2.0);
        when(localizacionService.actualizar(eq(1L), any(LocalizacionRequest.class))).thenReturn(responseEjemplo());

        mockMvc.perform(put("/api/v1/localizaciones/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/localizaciones/{id} debe eliminar la localización")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/localizaciones/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH .../gps debe registrar coordenadas desde el body")
    void debeRegistrarGPS() throws Exception {
        when(localizacionService.registrarGPS(1L, 11.0, -67.0)).thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/localizaciones/1/gps")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("latitud", 11.0, "longitud", -67.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("GPS registrado."));
    }

    @Test
    @DisplayName("PATCH .../direccion-manual debe registrar dirección desde el body")
    void debeRegistrarDireccionManual() throws Exception {
        when(localizacionService.registrarDireccionManual(1L, "Baruta", "Las Mercedes", "Calle 5", "ref"))
                .thenReturn(responseEjemplo());

        mockMvc.perform(patch("/api/v1/localizaciones/1/direccion-manual")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of(
                                "municipio", "Baruta", "sector", "Las Mercedes",
                                "direccion", "Calle 5", "referencia", "ref"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/localizaciones/{id}/validar debe retornar el resultado de validación")
    void debeValidarUbicacion() throws Exception {
        when(localizacionService.validarUbicacion(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/localizaciones/1/validar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}
