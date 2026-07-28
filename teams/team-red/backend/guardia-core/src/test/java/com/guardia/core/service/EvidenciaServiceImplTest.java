// Ruta destino: src/test/java/com/guardia/core/service/EvidenciaServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.HashStrategy;
import com.guardia.core.dto.request.EvidenciaRequest;
import com.guardia.core.dto.response.EvidenciaResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.Evidencia;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.repository.EvidenciaRepository;
import com.guardia.core.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link EvidenciaServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EvidenciaServiceImpl - Pruebas Unitarias")
class EvidenciaServiceImplTest {

    @Mock private EvidenciaRepository evidenciaRepository;
    @Mock private EscenaRepository escenaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private HashStrategy hashStrategy;

    @InjectMocks
    private EvidenciaServiceImpl evidenciaService;

    private Usuario investigadorEjemplo;
    private Escena escenaEjemplo;
    private Evidencia evidenciaEjemplo;
    private UUID investigadorId;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        investigadorId = UUID.randomUUID();
        investigadorEjemplo = Usuario.builder()
                .id(investigadorId).username("cruiz").password("hash").fullName("Carlos Ruiz").rol("ANALISTA").build();
        escenaEjemplo = Escena.builder().id(1L).levantadaPor(investigadorEjemplo).build();
        evidenciaEjemplo = Evidencia.builder()
                .id(100L).numeroItem("EV-001").tipo("ARMA").descripcion("Cuchillo")
                .escena(escenaEjemplo).investigador(investigadorEjemplo).hashIntegridad("hash123").build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear la evidencia asignando el número correlativo y el hash calculado")
        void debeCrearEvidenciaExitosamente() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(null, "ARMA", "Cuchillo", 1L, investigadorId, null);
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(2L);
            when(usuarioRepository.findById(investigadorId)).thenReturn(Optional.of(investigadorEjemplo));
            when(hashStrategy.calcular(anyString())).thenReturn("hash-calculado");
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.crear(request);

            // Assert
            assertThat(resultado.numeroItem()).isEqualTo("EV-003");
            assertThat(resultado.hashIntegridad()).isEqualTo("hash-calculado");
            assertThat(resultado.investigadorNombre()).isEqualTo("Carlos Ruiz");
            verify(hashStrategy).calcular("ARMA|Cuchillo");
        }

        @Test
        @DisplayName("Debe usar el hash provisto por el cliente cuando viene informado, sin recalcularlo")
        void debeUsarHashDelCliente() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(null, "ARMA", "Cuchillo", 1L, investigadorId, "hash-cliente-123");
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(0L);
            when(usuarioRepository.findById(investigadorId)).thenReturn(Optional.of(investigadorEjemplo));
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.crear(request);

            // Assert
            assertThat(resultado.hashIntegridad()).isEqualTo("hash-cliente-123");
            assertThat(resultado.numeroItem()).isEqualTo("EV-001");
            verifyNoInteractions(hashStrategy);
        }

        @Test
        @DisplayName("Debe usar el investigador que levantó la escena cuando no se especifica investigadorId")
        void debeUsarInvestigadorDeLaEscenaCuandoNoSeEspecifica() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(null, "ARMA", "Cuchillo", 1L, null, "hash-cliente");
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(0L);
            when(usuarioRepository.findById(investigadorId)).thenReturn(Optional.of(investigadorEjemplo));
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.crear(request);

            // Assert
            assertThat(resultado.investigadorNombre()).isEqualTo("Carlos Ruiz");
            verify(usuarioRepository).findById(investigadorId);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la escena no existe")
        void debeLanzarExcepcionCuandoEscenaNoExiste() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(null, "ARMA", "Cuchillo", 99L, investigadorId, null);
            when(escenaRepository.findByIdWithInvestigador(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verifyNoInteractions(evidenciaRepository);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigadorId especificado no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            UUID inexistente = UUID.randomUUID();
            EvidenciaRequest request = new EvidenciaRequest(null, "ARMA", "Cuchillo", 1L, inexistente, null);
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(0L);
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(evidenciaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("verificarHash()")
    class VerificarHash {

        @Test
        @DisplayName("Debe retornar true cuando el hash recalculado coincide con el almacenado")
        void debeRetornarTrueCuandoHashCoincide() {
            // Arrange
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(hashStrategy.calcular("ARMA|Cuchillo")).thenReturn("hash123");

            // Act
            boolean resultado = evidenciaService.verificarHash(100L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando el hash recalculado no coincide (evidencia alterada)")
        void debeRetornarFalseCuandoHashNoCoincide() {
            // Arrange
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(hashStrategy.calcular("ARMA|Cuchillo")).thenReturn("hash-diferente");

            // Act
            boolean resultado = evidenciaService.verificarHash(100L);

            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la evidencia no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            // Arrange
            when(evidenciaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.verificarHash(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar la evidencia cuando existe")
    void debeRetornarEvidenciaCuandoExiste() {
        // Arrange
        when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));

        // Act
        EvidenciaResponse resultado = evidenciaService.obtenerPorId(100L);

        // Assert
        assertThat(resultado.numeroItem()).isEqualTo("EV-001");
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(evidenciaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> evidenciaService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todas las evidencias registradas")
    void debeRetornarTodasLasEvidencias() {
        // Arrange
        when(evidenciaRepository.findAll()).thenReturn(List.of(evidenciaEjemplo));

        // Act
        List<EvidenciaResponse> resultado = evidenciaService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorEscena() debe delegar en el repositorio filtrando por escenaId")
    void debeRetornarEvidenciasPorEscena() {
        // Arrange
        when(evidenciaRepository.findByEscenaId(1L)).thenReturn(List.of(evidenciaEjemplo));

        // Act
        List<EvidenciaResponse> resultado = evidenciaService.obtenerPorEscena(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(evidenciaRepository).findByEscenaId(1L);
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar tipo, descripción y número de item")
        void debeActualizarEvidenciaExistente() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest("EV-999", "HUELLA", "Huella dactilar", 1L, investigadorId, null);
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.actualizar(100L, request);

            // Assert
            assertThat(resultado.tipo()).isEqualTo("HUELLA");
            assertThat(resultado.descripcion()).isEqualTo("Huella dactilar");
            assertThat(resultado.numeroItem()).isEqualTo("EV-999");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al actualizar una evidencia inexistente")
        void debeLanzarExcepcionAlActualizarInexistente() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest("EV-999", "HUELLA", "desc", 1L, investigadorId, null);
            when(evidenciaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.actualizar(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar la evidencia cuando existe")
        void debeEliminarEvidenciaExistente() {
            // Arrange
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));

            // Act
            evidenciaService.eliminar(100L);

            // Assert
            verify(evidenciaRepository).deleteById(100L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(evidenciaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.eliminar(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(evidenciaRepository, never()).deleteById(any());
        }
    }

    @Test
    @DisplayName("asignarNumero() debe actualizar el número de item de la evidencia")
    void debeAsignarNumero() {
        // Arrange
        when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));
        when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvidenciaResponse resultado = evidenciaService.asignarNumero(100L, "EV-777");

        // Assert
        assertThat(resultado.numeroItem()).isEqualTo("EV-777");
    }

    @Nested
    @DisplayName("firmarLevantamiento()")
    class FirmarLevantamiento {

        @Test
        @DisplayName("Debe firmar el levantamiento cuando el investigador existe")
        void debeFirmarLevantamientoExitosamente() {
            // Arrange
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(usuarioRepository.findById(investigadorId)).thenReturn(Optional.of(investigadorEjemplo));
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.firmarLevantamiento(100L, investigadorId);

            // Assert
            assertThat(resultado).isNotNull();
            verify(evidenciaRepository).save(evidenciaEjemplo);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            UUID inexistente = UUID.randomUUID();
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.firmarLevantamiento(100L, inexistente))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(evidenciaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("validarIntegridad()")
    class ValidarIntegridad {

        @Test
        @DisplayName("Debe retornar true cuando numeroItem, tipo y escena están presentes")
        void debeRetornarTrueCuandoValida() {
            // Arrange
            when(evidenciaRepository.findById(100L)).thenReturn(Optional.of(evidenciaEjemplo));

            // Act
            boolean resultado = evidenciaService.validarIntegridad(100L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el número de item")
        void debeRetornarFalseCuandoFaltaNumeroItem() {
            // Arrange
            Evidencia incompleta = Evidencia.builder().id(200L).tipo("ARMA").escena(escenaEjemplo).build();
            when(evidenciaRepository.findById(200L)).thenReturn(Optional.of(incompleta));

            // Act
            boolean resultado = evidenciaService.validarIntegridad(200L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }
}
