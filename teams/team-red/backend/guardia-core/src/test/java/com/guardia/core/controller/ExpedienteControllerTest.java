package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guardia.core.dto.request.CoordenadasRequest;
import com.guardia.core.dto.request.DelitoRequest;
import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.request.InvolucradosRequest;
import com.guardia.core.dto.request.SubDelitoRequest;
import com.guardia.core.dto.request.UbicacionRequest;
import com.guardia.core.dto.response.ExpedienteActivoResponse;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.dto.response.VerificacionHashResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.model.enums.EstadoExpediente;
import com.guardia.core.service.DeteccionModusOperandiService;
import com.guardia.core.service.ExpedienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpedienteController - Pruebas Unitarias")
class ExpedienteControllerTest {

    @Mock private ExpedienteService expedienteService;
    @Mock private DeteccionModusOperandiService deteccionModusOperandiService;

    @InjectMocks
    private ExpedienteController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ExpedienteRequest requestValido() {
        ExpedienteRequest request = new ExpedienteRequest();
        UbicacionRequest ubicacion = new UbicacionRequest();
        ubicacion.setMunicipio("Libertador");
        ubicacion.setSector("Catia");
        ubicacion.setDireccion("Av. Principal");
        ubicacion.setReferencia("Cerca de la plaza");
        CoordenadasRequest coordenadas = new CoordenadasRequest();
        coordenadas.setLatitud(10.5);
        coordenadas.setLongitud(-66.9);
        ubicacion.setCoordenadas(coordenadas);
        request.setUbicacion(ubicacion);

        DelitoRequest delito = new DelitoRequest();
        delito.setDelito("ROBO");
        SubDelitoRequest sub = new SubDelitoRequest();
        sub.setNombre("ROBO_AGRAVADO");
        delito.setSubDelito(sub);
        delito.setFechaHecho(LocalDate.of(2026, 1, 15));
        delito.setHoraInicioHecho(LocalTime.of(20, 0));
        delito.setHechoEnCurso(false);
        request.setDelitos(List.of(delito));

        request.setDescripcion("Descripción del hecho");

        InvolucradosRequest victima = new InvolucradosRequest();
        victima.setNombre("Maria Lopez");
        victima.setCedula("V-9999999");
        request.setVictimas(List.of(victima));

        return request;
    }

    private ExpedienteResponse expedienteResponseEjemplo() {
        return new ExpedienteResponse(1L, "EXP-2026-AAAA1111", EstadoExpediente.BORRADOR,
                null, null, "Descripción", null, null, null, null, null, null,
                List.of(), List.of(), null, null);
    }

    @Test
    @DisplayName("POST /api/v1/expedientes/registrar debe crear el expediente y responder 201")
    void debeRegistrarExpediente() throws Exception {
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/expedientes/registrar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.folio").value("EXP-2026-AAAA1111"));
    }

    @Test
    @DisplayName("POST /api/v1/expedientes/registrar debe responder 400 cuando falta la descripción")
    void debeRechazarSinDescripcion() throws Exception {
        ExpedienteRequest request = requestValido();
        request.setDescripcion("   ");

        mockMvc.perform(post("/api/v1/expedientes/registrar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/expedientes/{id}/sellar debe sellar el expediente con el agente indicado")
    void debeSellarExpediente() throws Exception {
        UUID agenteId = UUID.randomUUID();
        when(expedienteService.sellar(1L, agenteId)).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(patch("/api/v1/expedientes/1/sellar").param("agenteSelladorId", agenteId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expediente sellado."));
    }

    @Test
    @DisplayName("GET /api/v1/expedientes/{id}/verificar-integridad debe retornar el resultado de la verificación")
    void debeVerificarIntegridad() throws Exception {
        VerificacionHashResponse verificacion = new VerificacionHashResponse(
                1L, "EXP-2026-AAAA1111", true, "Integridad verificada.", "hashA", "hashA");
        when(expedienteService.verificarIntegridad(1L)).thenReturn(verificacion);

        mockMvc.perform(get("/api/v1/expedientes/1/verificar-integridad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.integro").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/expedientes debe listar los expedientes para el panel")
    void debeListarExpedientes() throws Exception {
        ExpedienteActivoResponse activo = new ExpedienteActivoResponse(
                "1", "EXP-2026-AAAA1111", "ROBO", null, null, null, "", "BORRADOR", false, "Libertador", "Catia");
        when(expedienteService.obtenerParaPanel("ACTIVO", "folio,desc")).thenReturn(List.of(activo));

        mockMvc.perform(get("/api/v1/expedientes").param("estatus", "ACTIVO").param("sort", "folio,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].folioCOPP").value("EXP-2026-AAAA1111"));
    }

    @Test
    @DisplayName("POST /api/v1/expedientes/{id}/reanalizar-mo debe encolar el análisis y responder 202")
    void debeReanalizarModusOperandi() throws Exception {
        mockMvc.perform(post("/api/v1/expedientes/1/reanalizar-mo"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Reanálisis de Modus Operandi encolado."));

        verify(deteccionModusOperandiService).analizarPatrones(1L);
    }
}
