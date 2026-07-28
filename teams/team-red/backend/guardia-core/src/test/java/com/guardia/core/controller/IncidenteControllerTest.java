package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.request.IncidenteRequest;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.model.enums.EstadoExpediente;
import com.guardia.core.service.ExpedienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas para {@link IncidenteController}, centradas en la lógica de mapeo
 * del payload del frontend ({@link IncidenteRequest}) hacia el contrato
 * interno ({@link ExpedienteRequest}).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IncidenteController - Pruebas Unitarias")
class IncidenteControllerTest {

    @Mock
    private ExpedienteService expedienteService;

    @InjectMocks
    private IncidenteController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ExpedienteResponse expedienteResponseEjemplo() {
        return new ExpedienteResponse(1L, "EXP-2026-AAAA1111", EstadoExpediente.BORRADOR,
                null, null, "Descripción", null, null, null, null, null, null,
                List.of(), List.of(), null, null);
    }

    private IncidenteRequest.UbicacionFrontRequest ubicacionCon(Double lat, Double lng) {
        IncidenteRequest.UbicacionFrontRequest u = new IncidenteRequest.UbicacionFrontRequest();
        u.setMunicipio("Libertador");
        u.setSector("Catia");
        u.setDireccion("Av. Principal");
        u.setReferencia("Cerca de la plaza");
        if (lat != null) {
            IncidenteRequest.CoordenadaFrontRequest c = new IncidenteRequest.CoordenadaFrontRequest();
            c.setLat(lat);
            c.setLng(lng);
            u.setCoordenadas(c);
        }
        return u;
    }

    private IncidenteRequest.DelitoFrontRequest delitoFrontCon(boolean hechoEnCurso, String horaFin) {
        IncidenteRequest.DelitoFrontRequest d = new IncidenteRequest.DelitoFrontRequest();
        d.setTipoDelito("ROBO");
        d.setSubtipoDelito("ROBO_AGRAVADO");
        d.setFechaHecho("2026-01-15");
        d.setHoraInicio("20:00");
        d.setHoraFin(horaFin);
        d.setHechoEnCurso(hechoEnCurso);
        return d;
    }

    private IncidenteRequest incidenteValido() {
        IncidenteRequest incidente = new IncidenteRequest();
        incidente.setUbicacion(ubicacionCon(10.5, -66.9));
        incidente.setDelitos(List.of(delitoFrontCon(false, "21:00")));
        incidente.setDescripcion("Descripción del incidente");
        incidente.setEsDenunciaFormal(true);

        IncidenteRequest.InvolucradoFrontRequest victima = new IncidenteRequest.InvolucradoFrontRequest();
        victima.setTipo("victima");
        victima.setNombre("Maria Isabel Lopez");
        victima.setIdentificacion("V-9999999");
        victima.setTelefono("0414-1234567");
        victima.setNacionalidad("Venezolana");
        victima.setDireccion("Calle Falsa 123");
        incidente.setInvolucrados(List.of(victima));

        IncidenteRequest.DenuncianteFrontRequest denunciante = new IncidenteRequest.DenuncianteFrontRequest();
        denunciante.setNombre("Pedro Diaz");
        denunciante.setIdentificacion("V-8888888");
        denunciante.setTelefono("0412-7654321");
        denunciante.setNacionalidad("Venezolana");
        denunciante.setDireccion("Otra calle");
        denunciante.setRelacionConCrimen("Testigo presencial");
        incidente.setDenunciante(denunciante);

        return incidente;
    }

    @Test
    @DisplayName("Debe registrar el incidente completo y responder 201")
    void debeRegistrarIncidenteCompleto() throws Exception {
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidenteValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Incidente registrado exitosamente."));
    }

    @Test
    @DisplayName("Debe mapear ubicación, delito y separar nombre/apellido de víctimas y denunciante")
    void debeMapearCamposCorrectamente() throws Exception {
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidenteValido())))
                .andExpect(status().isCreated());

        ArgumentCaptor<ExpedienteRequest> captor = ArgumentCaptor.forClass(ExpedienteRequest.class);
        verify(expedienteService).crear(captor.capture());
        ExpedienteRequest mapeado = captor.getValue();

        assertThat(mapeado.getUbicacion().getMunicipio()).isEqualTo("Libertador");
        assertThat(mapeado.getUbicacion().getCoordenadas().getLatitud()).isEqualTo(10.5);
        assertThat(mapeado.getUbicacion().getCoordenadas().getLongitud()).isEqualTo(-66.9);
        assertThat(mapeado.getDelitos()).hasSize(1);
        assertThat(mapeado.getDelitos().get(0).getDelito()).isEqualTo("ROBO");
        assertThat(mapeado.getDelitos().get(0).getSubDelito().getNombre()).isEqualTo("ROBO_AGRAVADO");
        assertThat(mapeado.getVictimas().get(0).getNombre()).isEqualTo("Maria Isabel");
        assertThat(mapeado.getVictimas().get(0).getApellido()).isEqualTo("Lopez");
        assertThat(mapeado.getDenunciante().getNombre()).isEqualTo("Pedro");
        assertThat(mapeado.getDenunciante().getApellido()).isEqualTo("Diaz");
        assertThat(mapeado.getDenunciante().getRelacionConHecho()).isEqualTo("Testigo presencial");
    }

    @Test
    @DisplayName("Debe usar coordenadas neutras (0,0) cuando el frontend no capturó GPS")
    void debeUsarCoordenadasNeutrasSinGPS() throws Exception {
        IncidenteRequest incidente = incidenteValido();
        incidente.setUbicacion(ubicacionCon(null, null));
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidente)))
                .andExpect(status().isCreated());

        ArgumentCaptor<ExpedienteRequest> captor = ArgumentCaptor.forClass(ExpedienteRequest.class);
        verify(expedienteService).crear(captor.capture());
        assertThat(captor.getValue().getUbicacion().getCoordenadas().getLatitud()).isEqualTo(0.0);
        assertThat(captor.getValue().getUbicacion().getCoordenadas().getLongitud()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("No debe mapear denunciante cuando el incidente no lo incluye")
    void noDebeMapearDenuncianteAusente() throws Exception {
        IncidenteRequest incidente = incidenteValido();
        incidente.setDenunciante(null);
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidente)))
                .andExpect(status().isCreated());

        ArgumentCaptor<ExpedienteRequest> captor = ArgumentCaptor.forClass(ExpedienteRequest.class);
        verify(expedienteService).crear(captor.capture());
        assertThat(captor.getValue().getDenunciante()).isNull();
    }

    @Test
    @DisplayName("Debe forzar horaFin en null cuando el hecho está en curso, aunque el frontend envíe un valor")
    void debeForzarHoraFinNulaCuandoHechoEnCurso() throws Exception {
        IncidenteRequest incidente = incidenteValido();
        incidente.setDelitos(List.of(delitoFrontCon(true, "23:00")));
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidente)))
                .andExpect(status().isCreated());

        ArgumentCaptor<ExpedienteRequest> captor = ArgumentCaptor.forClass(ExpedienteRequest.class);
        verify(expedienteService).crear(captor.capture());
        assertThat(captor.getValue().getDelitos().get(0).getHoraFin()).isNull();
        assertThat(captor.getValue().getDelitos().get(0).isHechoEnCurso()).isTrue();
    }

    @Test
    @DisplayName("Debe dejar el apellido vacío cuando el nombre completo es una sola palabra")
    void debeManejarNombreDeUnaSolaPalabra() throws Exception {
        IncidenteRequest incidente = incidenteValido();
        incidente.getInvolucrados().get(0).setNombre("Madonna");
        when(expedienteService.crear(any(ExpedienteRequest.class))).thenReturn(expedienteResponseEjemplo());

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidente)))
                .andExpect(status().isCreated());

        ArgumentCaptor<ExpedienteRequest> captor = ArgumentCaptor.forClass(ExpedienteRequest.class);
        verify(expedienteService).crear(captor.capture());
        assertThat(captor.getValue().getVictimas().get(0).getNombre()).isEqualTo("Madonna");
        assertThat(captor.getValue().getVictimas().get(0).getApellido()).isEmpty();
    }

    @Test
    @DisplayName("Debe responder 500 cuando la fecha del hecho viene en un formato inválido")
    void debeResponder500ConFechaInvalida() throws Exception {
        IncidenteRequest incidente = incidenteValido();
        incidente.getDelitos().get(0).setFechaHecho("15/01/2026"); // formato incorrecto (se espera yyyy-MM-dd)

        mockMvc.perform(post("/api/v1/incidentes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incidente)))
                .andExpect(status().isInternalServerError());
    }
}
