// Ruta destino: src/test/java/com/guardia/core/service/CasoServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.CasoRequest;
import com.guardia.core.dto.response.CasoResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Caso;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.CasoRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link CasoServiceImpl} (HU1 - "Agrupar delitos en un caso").
 *
 * <p>Refleja el contrato vigente: el creador se identifica por
 * {@code creadoPorUsername} (Usuario ahora usa id UUID sincronizado con el
 * servicio de SSO), y la respuesta siempre retorna {@code alertaOrigenId}
 * en null porque la entidad {@code Caso} no persiste ese campo.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CasoServiceImpl - Pruebas Unitarias")
class CasoServiceImplTest {

    @Mock private CasoRepository casoRepository;
    @Mock private ExpedienteRepository expedienteRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CasoServiceImpl casoService;

    private Usuario creador;
    private Expediente expedienteA;
    private Expediente expedienteB;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        creador = Usuario.builder()
                .id(UUID.randomUUID())
                .username("aperez")
                .password("hash")
                .fullName("Analista Pérez")
                .rol("ANALISTA")
                .build();
        expedienteA = Expediente.builder().id(10L).folio("EXP-2026-AAAA1111").build();
        expedienteB = Expediente.builder().id(20L).folio("EXP-2026-BBBB2222").build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe agrupar dos expedientes distintos y generar un código de caso correlativo")
        void debeCrearCasoExitosamente() {
            // Arrange
            CasoRequest request = new CasoRequest("aperez", List.of(10L, 20L), "Mismo patrón de actuación", null);
            when(usuarioRepository.findByUsername("aperez")).thenReturn(Optional.of(creador));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteA));
            when(expedienteRepository.findById(20L)).thenReturn(Optional.of(expedienteB));
            when(casoRepository.contarCasos()).thenReturn(3L);
            when(casoRepository.existsByCodigoCaso(anyString())).thenReturn(false);
            when(casoRepository.save(any(Caso.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            CasoResponse resultado = casoService.crear(request);

            // Assert
            assertThat(resultado.codigoCaso()).isEqualTo("CASO-%d-0004".formatted(Year.now().getValue()));
            assertThat(resultado.motivo()).isEqualTo("Mismo patrón de actuación");
            assertThat(resultado.creadoPor().username()).isEqualTo("aperez");
            assertThat(resultado.alertaOrigenId()).isNull();
            assertThat(resultado.expedientes()).hasSize(2);
            assertThat(expedienteA.getCaso()).isNotNull();
            assertThat(expedienteB.getCaso()).isNotNull();
            verify(expedienteRepository).save(expedienteA);
            verify(expedienteRepository).save(expedienteB);
        }

        @Test
        @DisplayName("Debe ignorar cualquier alertaOrigenId enviado: la respuesta siempre lo retorna en null")
        void debeIgnorarAlertaOrigenId() {
            // Arrange: aunque el cliente envíe un alertaOrigenId, el campo ya no existe en la entidad
            CasoRequest request = new CasoRequest("aperez", List.of(10L, 20L), "Motivo", UUID.randomUUID());
            when(usuarioRepository.findByUsername("aperez")).thenReturn(Optional.of(creador));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteA));
            when(expedienteRepository.findById(20L)).thenReturn(Optional.of(expedienteB));
            when(casoRepository.contarCasos()).thenReturn(0L);
            when(casoRepository.existsByCodigoCaso(anyString())).thenReturn(false);
            when(casoRepository.save(any(Caso.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            CasoResponse resultado = casoService.crear(request);

            // Assert
            assertThat(resultado.alertaOrigenId()).isNull();
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando se envían menos de dos expedientes distintos")
        void debeLanzarExcepcionConMenosDeDosExpedientes() {
            // Arrange: ids duplicados colapsan a un único id tras el Set
            CasoRequest request = new CasoRequest("aperez", List.of(10L, 10L), "Motivo", null);

            // Act & Assert
            assertThatThrownBy(() -> casoService.crear(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Un caso debe agrupar al menos dos expedientes distintos.");
            verifyNoInteractions(usuarioRepository, expedienteRepository, casoRepository);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el usuario creador no existe")
        void debeLanzarExcepcionCuandoCreadorNoExiste() {
            // Arrange
            CasoRequest request = new CasoRequest("desconocido", List.of(10L, 20L), "Motivo", null);
            when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> casoService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con username 'desconocido' no encontrado.");
            verify(expedienteRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando alguno de los expedientes no existe")
        void debeLanzarExcepcionCuandoExpedienteNoExiste() {
            // Arrange
            CasoRequest request = new CasoRequest("aperez", List.of(10L, 999L), "Motivo", null);
            when(usuarioRepository.findByUsername("aperez")).thenReturn(Optional.of(creador));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteA));
            when(expedienteRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> casoService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(casoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando un expediente ya pertenece a otro caso")
        void debeLanzarExcepcionCuandoExpedienteYaAgrupado() {
            // Arrange
            Caso casoPrevio = Caso.builder().id(500L).codigoCaso("CASO-2026-0001").build();
            expedienteA.setCaso(casoPrevio);
            CasoRequest request = new CasoRequest("aperez", List.of(10L, 20L), "Motivo", null);
            when(usuarioRepository.findByUsername("aperez")).thenReturn(Optional.of(creador));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteA));
            when(expedienteRepository.findById(20L)).thenReturn(Optional.of(expedienteB));

            // Act & Assert
            assertThatThrownBy(() -> casoService.crear(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("EXP-2026-AAAA1111")
                    .hasMessageContaining("ya pertenecen a otro caso");
            verify(casoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe incrementar el correlativo cuando el código candidato ya existe (colisión)")
        void debeEvitarColisionDeCodigoDeCaso() {
            // Arrange
            CasoRequest request = new CasoRequest("aperez", List.of(10L, 20L), "Motivo", null);
            when(usuarioRepository.findByUsername("aperez")).thenReturn(Optional.of(creador));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteA));
            when(expedienteRepository.findById(20L)).thenReturn(Optional.of(expedienteB));
            when(casoRepository.contarCasos()).thenReturn(0L);
            when(casoRepository.existsByCodigoCaso("CASO-%d-0001".formatted(Year.now().getValue()))).thenReturn(true);
            when(casoRepository.existsByCodigoCaso("CASO-%d-0002".formatted(Year.now().getValue()))).thenReturn(false);
            when(casoRepository.save(any(Caso.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            CasoResponse resultado = casoService.crear(request);

            // Assert
            assertThat(resultado.codigoCaso()).isEqualTo("CASO-%d-0002".formatted(Year.now().getValue()));
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar el caso cuando existe")
    void debeRetornarCasoCuandoExiste() {
        // Arrange
        Caso caso = Caso.builder().id(1L).codigoCaso("CASO-2026-0001").motivo("Motivo")
                .creadoPor(creador).expedientes(List.of(expedienteA, expedienteB)).build();
        when(casoRepository.findById(1L)).thenReturn(Optional.of(caso));

        // Act
        CasoResponse resultado = casoService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.codigoCaso()).isEqualTo("CASO-2026-0001");
        assertThat(resultado.expedientes()).hasSize(2);
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoCasoNoExiste() {
        // Arrange
        when(casoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> casoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los casos registrados")
    void debeRetornarTodosLosCasos() {
        // Arrange
        Caso caso = Caso.builder().id(1L).codigoCaso("CASO-2026-0001").motivo("Motivo")
                .creadoPor(creador).expedientes(List.of()).build();
        when(casoRepository.findAll()).thenReturn(List.of(caso));

        // Act
        List<CasoResponse> resultado = casoService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }
}
