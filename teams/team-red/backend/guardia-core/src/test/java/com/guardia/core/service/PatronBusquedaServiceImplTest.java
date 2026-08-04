// Ruta destino: src/test/java/com/guardia/core/service/PatronBusquedaServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.PatronBusquedaRequest;
import com.guardia.core.dto.response.PatronBusquedaResultado;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.FirmaConductual;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.FirmaConductualRepository;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link PatronBusquedaServiceImpl}.
 *
 * <p>Al igual que en {@link DeteccionModusOperandiServiceImplTest}, los campos
 * {@code @Value} no se resuelven fuera de un {@code ApplicationContext} real,
 * por lo que se inyectan manualmente con {@link ReflectionTestUtils}.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PatronBusquedaServiceImpl - Pruebas Unitarias")
class PatronBusquedaServiceImplTest {

    @Mock private PropuestaModusOperandiRepository propuestaRepository;
    @Mock private FirmaConductualRepository firmaConductualRepository;
    @Mock private ExpedienteRepository expedienteRepository;
    @Mock private EmbeddingModel embeddingModel;

    @InjectMocks
    private PatronBusquedaServiceImpl service;

    private Usuario investigador;
    private TipoDelito robo;

    @BeforeEach
    void setUp() {
        // Arrange común: los campos @Value no se resuelven sin un ApplicationContext real.
        ReflectionTestUtils.setField(service, "topKPorCriterio", 50);
        ReflectionTestUtils.setField(service, "limiteMaximo", 100);

        investigador = Usuario.builder().id(UUID.randomUUID()).fullName("Inv. Pérez").build();
        robo = TipoDelito.builder().id(1L).nombre("ROBO").build();
    }

    private Expediente expediente(Long id, String folio) {
        return Expediente.builder().id(id).folio(folio).tipoDelito(robo).creadoPor(investigador).build();
    }

    @Nested
    @DisplayName("buscar()")
    class Buscar {

        @Test
        @DisplayName("Debe lanzar BusinessException cuando no se indica ningún criterio de búsqueda")
        void debeLanzarExcepcionSinCriterios() {
            // Arrange
            PatronBusquedaRequest request = new PatronBusquedaRequest(null, "   ", null);

            // Act & Assert
            assertThatThrownBy(() -> service.buscar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("al menos un criterio");
            verifyNoInteractions(embeddingModel, propuestaRepository, firmaConductualRepository, expedienteRepository);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando ningún criterio arroja coincidencias")
        void debeRetornarListaVaciaSinCoincidencias() {
            // Arrange
            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.buscarPorEmbeddingMOValidado(any(float[].class), anyList(), any(Pageable.class)))
                    .thenReturn(List.of());

            // Act
            List<PatronBusquedaResultado> resultado =
                    service.buscar(new PatronBusquedaRequest("robo a mano armada", null, null));

            // Assert
            assertThat(resultado).isEmpty();
            verifyNoInteractions(expedienteRepository);
        }

        @Test
        @DisplayName("Debe buscar sólo por MO validado cuando sólo se indica textoMO, sin tocar firma conductual")
        void debeBuscarSoloPorMO() {
            // Arrange: distancia 0.2 -> similitud 80%
            Expediente exp = expediente(1L, "EXP-2026-AAAA1111");
            PropuestaModusOperandi propuestaValidada = PropuestaModusOperandi.builder()
                    .id(10L).expediente(exp).estado(EstadoPropuestaMO.APROBADA).build();

            when(embeddingModel.embed("entra por la ventana trasera")).thenReturn(new float[]{0.2f});
            when(propuestaRepository.buscarPorEmbeddingMOValidado(
                    any(float[].class),
                    eq(List.of(EstadoPropuestaMO.APROBADA, EstadoPropuestaMO.CORREGIDA)),
                    any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{propuestaValidada, 0.2}));
            when(expedienteRepository.findAllById(List.of(1L))).thenReturn(List.of(exp));

            // Act
            List<PatronBusquedaResultado> resultado =
                    service.buscar(new PatronBusquedaRequest("entra por la ventana trasera", null, null));

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).folio()).isEqualTo("EXP-2026-AAAA1111");
            assertThat(resultado.get(0).similitudPorcentaje()).isCloseTo(80.0, within(0.001));
            assertThat(resultado.get(0).investigadorAsignado()).isEqualTo("Inv. Pérez");
            verifyNoInteractions(firmaConductualRepository);
        }

        @Test
        @DisplayName("Debe promediar el puntaje cuando un expediente coincide en ambos criterios")
        void debeCombinarPuntajesCuandoCoincideEnAmbos() {
            // Arrange: MO -> distancia 0.2 (80%), firma -> distancia 0.0 (100%) => promedio 90%
            Expediente exp = expediente(2L, "EXP-2026-BBBB2222");
            PropuestaModusOperandi propuestaValidada = PropuestaModusOperandi.builder()
                    .id(11L).expediente(exp).estado(EstadoPropuestaMO.CORREGIDA).build();
            FirmaConductual firma = FirmaConductual.builder().id(20L).expediente(exp).build();

            when(embeddingModel.embed("caracteristicas comunes del MO")).thenReturn(new float[]{0.2f});
            when(embeddingModel.embed("elementos distintivos")).thenReturn(new float[]{0.0f});
            when(propuestaRepository.buscarPorEmbeddingMOValidado(any(float[].class), anyList(), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{propuestaValidada, 0.2}));
            when(firmaConductualRepository.buscarPorEmbedding(any(float[].class), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{firma, 0.0}));
            when(expedienteRepository.findAllById(List.of(2L))).thenReturn(List.of(exp));

            // Act
            List<PatronBusquedaResultado> resultado = service.buscar(
                    new PatronBusquedaRequest("caracteristicas comunes del MO", "elementos distintivos", null));

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).similitudPorcentaje()).isCloseTo(90.0, within(0.001));
        }

        @Test
        @DisplayName("Debe usar el puntaje disponible cuando un expediente sólo aparece en un criterio")
        void debeUsarPuntajeDisponibleCuandoSoloCoincideEnUnCriterio() {
            // Arrange: expA sólo tiene MO indexado, expB sólo tiene firma indexada
            Expediente expA = expediente(3L, "EXP-2026-CCCC3333");
            Expediente expB = expediente(4L, "EXP-2026-DDDD4444");
            PropuestaModusOperandi propuestaA = PropuestaModusOperandi.builder()
                    .id(12L).expediente(expA).estado(EstadoPropuestaMO.APROBADA).build();
            FirmaConductual firmaB = FirmaConductual.builder().id(21L).expediente(expB).build();

            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.buscarPorEmbeddingMOValidado(any(float[].class), anyList(), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{propuestaA, 0.1})); // 90%
            when(firmaConductualRepository.buscarPorEmbedding(any(float[].class), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{firmaB, 0.3})); // 70%
            when(expedienteRepository.findAllById(anyList())).thenReturn(List.of(expA, expB));

            // Act
            List<PatronBusquedaResultado> resultado =
                    service.buscar(new PatronBusquedaRequest("patrón MO", "patrón firma", null));

            // Assert: ninguno de los dos se promedia con el otro criterio (no comparten expediente)
            assertThat(resultado).hasSize(2);
            assertThat(resultado)
                    .extracting(PatronBusquedaResultado::folio, PatronBusquedaResultado::similitudPorcentaje)
                    .contains(
                            org.assertj.core.groups.Tuple.tuple("EXP-2026-CCCC3333", 90.0),
                            org.assertj.core.groups.Tuple.tuple("EXP-2026-DDDD4444", 70.0));
        }

        @Test
        @DisplayName("Debe ordenar los resultados de forma descendente por similitud y respetar el límite")
        void debeOrdenarDescendenteYLimitar() {
            // Arrange: tres expedientes con similitudes 60%, 95% y 80%
            Expediente exp1 = expediente(1L, "EXP-2026-0001");
            Expediente exp2 = expediente(2L, "EXP-2026-0002");
            Expediente exp3 = expediente(3L, "EXP-2026-0003");

            PropuestaModusOperandi p1 = PropuestaModusOperandi.builder().id(1L).expediente(exp1).estado(EstadoPropuestaMO.APROBADA).build();
            PropuestaModusOperandi p2 = PropuestaModusOperandi.builder().id(2L).expediente(exp2).estado(EstadoPropuestaMO.APROBADA).build();
            PropuestaModusOperandi p3 = PropuestaModusOperandi.builder().id(3L).expediente(exp3).estado(EstadoPropuestaMO.APROBADA).build();

            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.buscarPorEmbeddingMOValidado(any(float[].class), anyList(), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(
                            new Object[]{p1, 0.4},  // 60%
                            new Object[]{p2, 0.05}, // 95%
                            new Object[]{p3, 0.2}   // 80%
                    ));
            when(expedienteRepository.findAllById(List.of(2L, 3L)))
                    .thenReturn(List.of(exp2, exp3));

            // Act: limite = 2, debe devolver sólo los dos mejores, en orden descendente
            List<PatronBusquedaResultado> resultado =
                    service.buscar(new PatronBusquedaRequest("patrón de robo", null, 2));

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).folio()).isEqualTo("EXP-2026-0002");
            assertThat(resultado.get(1).folio()).isEqualTo("EXP-2026-0003");
            assertThat(resultado.get(0).similitudPorcentaje())
                    .isGreaterThan(resultado.get(1).similitudPorcentaje());
        }

        @Test
        @DisplayName("Debe acotar el límite solicitado al límite máximo configurado")
        void debeAcotarLimiteAlMaximoConfigurado() {
            // Arrange
            ReflectionTestUtils.setField(service, "limiteMaximo", 1);
            Expediente exp1 = expediente(1L, "EXP-2026-0001");
            Expediente exp2 = expediente(2L, "EXP-2026-0002");
            PropuestaModusOperandi p1 = PropuestaModusOperandi.builder().id(1L).expediente(exp1).estado(EstadoPropuestaMO.APROBADA).build();
            PropuestaModusOperandi p2 = PropuestaModusOperandi.builder().id(2L).expediente(exp2).estado(EstadoPropuestaMO.APROBADA).build();

            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.buscarPorEmbeddingMOValidado(any(float[].class), anyList(), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{p1, 0.3}, new Object[]{p2, 0.1}));
            when(expedienteRepository.findAllById(List.of(2L))).thenReturn(List.of(exp2));

            // Act: se solicitan 50 resultados, pero el máximo configurado es 1
            List<PatronBusquedaResultado> resultado =
                    service.buscar(new PatronBusquedaRequest("patrón de robo", null, 50));

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).folio()).isEqualTo("EXP-2026-0002");
        }
    }
}
