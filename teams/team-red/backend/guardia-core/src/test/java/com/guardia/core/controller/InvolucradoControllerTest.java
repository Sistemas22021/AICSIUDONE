package com.guardia.core.controller;

import com.guardia.core.dto.response.InvolucradoResponse;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.service.InvolucradoService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvolucradoController - Pruebas Unitarias")
class InvolucradoControllerTest {

    @Mock
    private InvolucradoService involucradoService;

    @InjectMocks
    private InvolucradoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private InvolucradoResponse responseEjemplo() {
        return new InvolucradoResponse(1L, "Maria Lopez", "V-9999999", "0414-1234567",
                "Venezolana", "Calle Falsa 123", TipoRol.VICTIMA, "Víctima directa");
    }

    @Test
    @DisplayName("GET /api/v1/involucrados debe retornar todos los involucrados")
    void debeObtenerTodos() throws Exception {
        when(involucradoService.obtenerTodos()).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/involucrados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/involucrados/{id} debe retornar el involucrado solicitado")
    void debeObtenerPorId() throws Exception {
        when(involucradoService.obtenerPorId(1L)).thenReturn(responseEjemplo());

        mockMvc.perform(get("/api/v1/involucrados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Maria Lopez"));
    }

    @Test
    @DisplayName("GET /api/v1/involucrados/rol/{rol} debe filtrar por rol")
    void debeObtenerPorRol() throws Exception {
        when(involucradoService.obtenerPorRol(TipoRol.VICTIMA)).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/involucrados/rol/VICTIMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].rol").value("VICTIMA"));
    }

    @Test
    @DisplayName("GET /api/v1/involucrados/expediente/{id} debe filtrar por expediente")
    void debeObtenerPorExpediente() throws Exception {
        when(involucradoService.obtenerPorExpediente(5L)).thenReturn(List.of(responseEjemplo()));

        mockMvc.perform(get("/api/v1/involucrados/expediente/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/involucrados/{id} debe eliminar el involucrado")
    void debeEliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/involucrados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Involucrado eliminado."));
    }
}
