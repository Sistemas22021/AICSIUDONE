// Ruta destino: src/test/java/com/guardia/core/service/DeteccionModusOperandiServiceImplTest.java
//
// NOTA DE AUDITORÍA (actualizada): en versiones anteriores del proyecto este
// servicio no tenía ningún punto de entrada real. Con la reestructuración
// vigente, `ExpedienteRegistradoEventListener` (en el paquete raíz
// com.guardia.core) escucha `ExpedienteRegistradoEvent` tras confirmarse la
// transacción y llama a `DeteccionModusOperandiService.analizarPatrones(id)`,
// por lo que este servicio es ahora el pipeline de detección de MO realmente
// conectado al flujo de registro de expedientes (HU2).
package com.guardia.core.service;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link DeteccionModusOperandiServiceImpl}.
 *
 * <p>Como la clase usa campos {@code @Value} (no resueltos por Mockito fuera
 * de un contexto de Spring) y construye internamente un {@code ChatClient}
 * real a partir de un {@code ChatModel} mockeado, este test: (1) inyecta los
 * valores de configuración con {@link ReflectionTestUtils}, y (2) fuerza el
 * camino de error del LLM mockeando {@code chatModel.call(...)} para que
 * lance una excepción — así se ejercita el flujo completo de persistencia sin
 * depender de construir una respuesta JSON real del modelo.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeteccionModusOperandiServiceImpl - Pruebas Unitarias")
class DeteccionModusOperandiServiceImplTest {

    @Mock private ExpedienteRepository expedienteRepository;
    @Mock private PropuestaModusOperandiRepository propuestaRepository;
    @Mock private EmbeddingModel embeddingModel;
    @Mock private ChatModel chatModel;

    private DeteccionModusOperandiServiceImpl service;

    private Expediente expedienteEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange: construcción manual porque el constructor de la clase envuelve
        // el ChatModel mockeado en un ChatClient real (no aplica @InjectMocks aquí).
        service = new DeteccionModusOperandiServiceImpl(
                expedienteRepository, propuestaRepository, embeddingModel, chatModel);

        // Los campos @Value no se resuelven fuera de un ApplicationContext real.
        ReflectionTestUtils.setField(service, "topK", 5);
        ReflectionTestUtils.setField(service, "umbralSimilitudCandidato", 70.0);
        ReflectionTestUtils.setField(service, "nombreModeloEmbedding", "nomic-embed-text");
        ReflectionTestUtils.setField(service, "nombreModeloChat", "llama3.2");

        expedienteEjemplo = Expediente.builder()
                .id(1L).folio("EXP-2026-AAAA1111").descripcionHecho("Robo con forzado de cerradura").build();
    }

    @Nested
    @DisplayName("analizarPatrones()")
    class AnalizarPatrones {

        @Test
        @DisplayName("Debe omitir el análisis (sin lanzar) cuando el expediente no existe")
        void debeOmitirCuandoExpedienteNoExiste() {
            // Arrange
            when(expedienteRepository.findById(99L)).thenReturn(Optional.empty());

            // Act: no debe propagar la excepción (se captura y se loguea internamente)
            service.analizarPatrones(99L);

            // Assert
            verifyNoInteractions(embeddingModel, propuestaRepository);
        }

        @Test
        @DisplayName("Debe omitir el análisis cuando el expediente no tiene descripción del hecho")
        void debeOmitirCuandoSinDescripcion() {
            // Arrange
            Expediente sinDescripcion = Expediente.builder().id(2L).folio("EXP-2026-CCCC3333")
                    .descripcionHecho("   ").build();
            when(expedienteRepository.findById(2L)).thenReturn(Optional.of(sinDescripcion));

            // Act
            service.analizarPatrones(2L);

            // Assert
            verifyNoInteractions(embeddingModel);
            verify(propuestaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe generar embedding y registrar SIN_COINCIDENCIAS cuando no hay expedientes similares")
        void debeRegistrarSinCoincidencias() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(embeddingModel.embed("Robo con forzado de cerradura")).thenReturn(new float[]{0.1f, 0.2f});
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.empty());
            when(expedienteRepository.buscarSimilaresPorEmbedding(eq(1L), any(float[].class), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of());

            // Act
            service.analizarPatrones(1L);

            // Assert
            verify(expedienteRepository).save(expedienteEjemplo);
            ArgumentCaptor<PropuestaModusOperandi> captor = ArgumentCaptor.forClass(PropuestaModusOperandi.class);
            verify(propuestaRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo(EstadoPropuestaMO.SIN_COINCIDENCIAS);
            assertThat(captor.getValue().getVersion()).isEqualTo(1);
            verify(chatModel, never()).call(any(Prompt.class));
        }

        @Test
        @DisplayName("Debe filtrar candidatos por debajo del umbral de similitud configurado")
        void debeFiltrarPorUmbralDeSimilitud() {
            // Arrange: distancia coseno 0.5 -> similitud 50%, por debajo del umbral (70%)
            Expediente similarLejano = Expediente.builder().id(2L).folio("EXP-2026-LEJANO").build();
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.empty());
            when(expedienteRepository.buscarSimilaresPorEmbedding(eq(1L), any(float[].class), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{similarLejano, 0.5}));

            // Act
            service.analizarPatrones(1L);

            // Assert: al quedar todos los candidatos filtrados, se registra SIN_COINCIDENCIAS
            ArgumentCaptor<PropuestaModusOperandi> captor = ArgumentCaptor.forClass(PropuestaModusOperandi.class);
            verify(propuestaRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo(EstadoPropuestaMO.SIN_COINCIDENCIAS);
        }

        @Test
        @DisplayName("Debe generar la propuesta con el fallback de IA cuando el LLM falla, y marcar la anterior como no vigente")
        void debeGenerarPropuestaConFallbackDeIA() {
            // Arrange: un candidato con similitud suficiente (distancia 0.1 -> 90%)
            Expediente similarCercano = Expediente.builder().id(2L).folio("EXP-2026-CERCANO").build();
            PropuestaModusOperandi vigenteAnterior = PropuestaModusOperandi.builder()
                    .id(50L).version(1).vigente(true).revisadoPorExperto(false).build();

            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.of(vigenteAnterior));
            when(expedienteRepository.buscarSimilaresPorEmbedding(eq(1L), any(float[].class), any(Pageable.class)))
                    .thenReturn(List.<Object[]>of(new Object[]{similarCercano, 0.1}));
            when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("Ollama no disponible"));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.analizarPatrones(1L);

            // Assert
            ArgumentCaptor<PropuestaModusOperandi> captor = ArgumentCaptor.forClass(PropuestaModusOperandi.class);
            verify(propuestaRepository, times(2)).save(captor.capture());

            PropuestaModusOperandi guardadaAnterior = captor.getAllValues().get(0);
            assertThat(guardadaAnterior.getId()).isEqualTo(50L); // la vigente anterior, ahora reemplazada

            PropuestaModusOperandi nueva = captor.getAllValues().get(1);
            assertThat(nueva.getEstado()).isEqualTo(EstadoPropuestaMO.PENDIENTE);
            assertThat(nueva.getVersion()).isEqualTo(2);
            assertThat(nueva.getNivelConfianza()).isEqualTo(0.0);
            assertThat(nueva.getCaracteristicasComunes())
                    .isEqualTo("No se pudo generar el análisis automáticamente");
        }

        @Test
        @DisplayName("Debe omitir el análisis cuando la propuesta vigente ya fue revisada por un experto")
        void debeOmitirCuandoYaRevisadaPorExperto() {
            // Arrange
            PropuestaModusOperandi revisada = PropuestaModusOperandi.builder()
                    .id(10L).version(3).vigente(true).revisadoPorExperto(true).build();
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(embeddingModel.embed(anyString())).thenReturn(new float[]{0.1f});
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.of(revisada));

            // Act
            service.analizarPatrones(1L);

            // Assert: HU3/HU5 CA4 — el sistema no sobrescribe una propuesta ya validada por un experto
            verify(propuestaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("compararExpedientes()")
    class CompararExpedientes {

        @Test
        @DisplayName("Debe retornar 0.0 sin consultar el repositorio cuando alguno de los expedientes es null")
        void debeRetornarCeroConExpedienteNulo() {
            // Act
            double resultado = service.compararExpedientes(null, expedienteEjemplo);

            // Assert
            assertThat(resultado).isEqualTo(0.0);
            verifyNoInteractions(expedienteRepository);
        }

        @Test
        @DisplayName("Debe retornar 0.0 sin consultar el repositorio cuando algún id es null")
        void debeRetornarCeroConIdNulo() {
            // Arrange
            Expediente sinId = Expediente.builder().folio("SIN-ID").build();

            // Act
            double resultado = service.compararExpedientes(sinId, expedienteEjemplo);

            // Assert
            assertThat(resultado).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Debe convertir la distancia coseno del repositorio a similitud porcentual")
        void debeConvertirDistanciaASimilitud() {
            // Arrange
            Expediente otro = Expediente.builder().id(2L).folio("EXP-2026-BBBB2222").build();
            when(expedienteRepository.calcularDistanciaCoseno(1L, 2L)).thenReturn(0.2);

            // Act
            double resultado = service.compararExpedientes(expedienteEjemplo, otro);

            // Assert: (1 - 0.2) * 100 = 80.0
            assertThat(resultado).isCloseTo(80.0, within(0.001));
        }

        @Test
        @DisplayName("Debe retornar 0.0 cuando el repositorio no puede calcular la distancia (algún embedding ausente)")
        void debeRetornarCeroCuandoRepositorioRetornaNull() {
            // Arrange
            Expediente otro = Expediente.builder().id(2L).folio("EXP-2026-BBBB2222").build();
            when(expedienteRepository.calcularDistanciaCoseno(1L, 2L)).thenReturn(null);

            // Act
            double resultado = service.compararExpedientes(expedienteEjemplo, otro);

            // Assert
            assertThat(resultado).isEqualTo(0.0);
        }
    }
}
