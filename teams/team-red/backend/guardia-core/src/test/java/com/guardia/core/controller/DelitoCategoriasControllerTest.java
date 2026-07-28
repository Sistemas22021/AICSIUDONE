package com.guardia.core.controller;

import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.dto.response.TipoDelitoResponse;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas para {@link DelitoCategoriasController}, con énfasis en la lógica
 * de transformación ("slugify") que vive directamente en el controlador.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DelitoCategoriasController - Pruebas Unitarias")
class DelitoCategoriasControllerTest {

    @Mock
    private TipoDelitoService tipoDelitoService;

    @InjectMocks
    private DelitoCategoriasController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Debe convertir nombre y subtipos al formato slug esperado por el frontend")
    void debeConvertirAlFormatoDelFrontend() throws Exception {
        SubtipoDelitoResponse subtipo = new SubtipoDelitoResponse(1L, "Homicidio Culposo", null, 1L, "Homicidio Calificado");
        TipoDelitoResponse tipo = new TipoDelitoResponse(1L, "Homicidio Calificado", null, true, List.of(subtipo));
        when(tipoDelitoService.obtenerTodos()).thenReturn(List.of(tipo));

        mockMvc.perform(get("/api/v1/delitos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value("homicidio_calificado"))
                .andExpect(jsonPath("$[0].label").value("HOMICIDIO CALIFICADO"))
                .andExpect(jsonPath("$[0].subtipos[0].value").value("homicidio_culposo"))
                .andExpect(jsonPath("$[0].subtipos[0].label").value("Homicidio Culposo"));
    }

    @Test
    @DisplayName("Debe normalizar tildes y la letra ñ al construir el slug")
    void debeNormalizarTildesYEnie() throws Exception {
        TipoDelitoResponse tipo = new TipoDelitoResponse(2L, "Daño a la Propiedad Ajena", null, false, List.of());
        when(tipoDelitoService.obtenerTodos()).thenReturn(List.of(tipo));

        mockMvc.perform(get("/api/v1/delitos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value("dano_a_la_propiedad_ajena"));
    }

    @Test
    @DisplayName("Debe retornar una lista de subtipos vacía cuando el tipo no tiene subtipos")
    void debeRetornarSubtiposVacios() throws Exception {
        TipoDelitoResponse tipo = new TipoDelitoResponse(3L, "Robo", null, false, null);
        when(tipoDelitoService.obtenerTodos()).thenReturn(List.of(tipo));

        mockMvc.perform(get("/api/v1/delitos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subtipos").isArray())
                .andExpect(jsonPath("$[0].subtipos.length()").value(0));
    }

    @Test
    @DisplayName("Debe retornar una lista vacía cuando no hay tipos de delito registrados")
    void debeRetornarListaVaciaSinTipos() throws Exception {
        when(tipoDelitoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/delitos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
