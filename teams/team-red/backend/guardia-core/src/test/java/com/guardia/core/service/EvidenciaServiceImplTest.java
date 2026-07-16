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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link EvidenciaServiceImpl}.
 * Se mockea {@link HashStrategy} para aislar el cálculo criptográfico real del SHA-256.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EvidenciaServiceImpl - Pruebas Unitarias")
class EvidenciaServiceImplTest {

    @Mock
    private EvidenciaRepository evidenciaRepository;

    @Mock
    private EscenaRepository escenaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HashStrategy hashStrategy;

    @InjectMocks
    private EvidenciaServiceImpl evidenciaService;

    private Escena escenaEjemplo;
    private Usuario investigadorEjemplo;
    private Evidencia evidenciaEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        investigadorEjemplo = Usuario.builder().id(1L).nombre("Carlos Ruiz").build();
        escenaEjemplo = Escena.builder().id(1L).levantadaPor(investigadorEjemplo).build();
        evidenciaEjemplo = Evidencia.builder()
                .id(1L)
                .numeroItem("EV-001")
                .tipo("Arma blanca")
                .descripcion("Cuchillo con manchas de sangre")
                .escena(escenaEjemplo)
                .hashIntegridad("hash-calculado")
                .investigador(investigadorEjemplo)
                .build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear la evidencia asignando el número de item consecutivo y el investigador indicado")
        void debeCrearEvidenciaConInvestigadorExplicito() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(
                    null, "Arma blanca", "Cuchillo con manchas de sangre", 1L, 1L, null);
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(0L);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(investigadorEjemplo));
            when(hashStrategy.calcular(anyString())).thenReturn("hash-calculado");
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.crear(request);

            // Assert
            assertThat(resultado.numeroItem()).isEqualTo("EV-001");
            assertThat(resultado.hashIntegridad()).isEqualTo("hash-calculado");
            assertThat(resultado.investigadorNombre()).isEqualTo("Carlos Ruiz");
        }

        @Test
        @DisplayName("Debe usar el investigador de la escena cuando no se especifica investigadorId")
        void debeUsarInvestigadorDeLaEscenaCuandoNoSeEspecifica() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(
                    null, "Huella dactilar", "Huella en vaso", 1L, null, null);
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(2L);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(investigadorEjemplo));
            when(hashStrategy.calcular(anyString())).thenReturn("hash-generado");
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.crear(request);

            // Assert: el consecutivo debe ser total + 1
            assertThat(resultado.numeroItem()).isEqualTo("EV-003");
            assertThat(resultado.investigadorNombre()).isEqualTo("Carlos Ruiz");
        }

        @Test
        @DisplayName("Debe usar el hash enviado por el cliente cuando viene informado")
        void debeUsarHashDelClienteCuandoViene() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(
                    null, "Fotografía", "Foto de la escena", 1L, 1L, "hash-del-cliente-sha256");
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(0L);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(investigadorEjemplo));
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.crear(request);

            // Assert
            assertThat(resultado.hashIntegridad()).isEqualTo("hash-del-cliente-sha256");
            verify(hashStrategy, never()).calcular(anyString());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la escena no existe")
        void debeLanzarExcepcionCuandoEscenaNoExiste() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(
                    null, "Arma", "desc", 99L, 1L, null);
            when(escenaRepository.findByIdWithInvestigador(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Escena con id 99 no encontrado.");
            verify(evidenciaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigadorId indicado no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            EvidenciaRequest request = new EvidenciaRequest(
                    null, "Arma", "desc", 1L, 77L, null);
            when(escenaRepository.findByIdWithInvestigador(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(evidenciaRepository.countByEscenaId(1L)).thenReturn(0L);
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con id 77 no encontrado.");
        }
    }

    @Nested
    @DisplayName("verificarHash()")
    class VerificarHash {

        @Test
        @DisplayName("Debe retornar true cuando el hash recalculado coincide con el almacenado")
        void debeRetornarTrueCuandoHashCoincide() {
            // Arrange
            evidenciaEjemplo.setTipo("Arma blanca");
            evidenciaEjemplo.setDescripcion("Cuchillo con manchas de sangre");
            when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(hashStrategy.calcular("Arma blanca|Cuchillo con manchas de sangre")).thenReturn("hash-calculado");

            // Act
            boolean resultado = evidenciaService.verificarHash(1L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando el hash recalculado no coincide")
        void debeRetornarFalseCuandoHashNoCoincide() {
            // Arrange
            when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(hashStrategy.calcular(anyString())).thenReturn("hash-diferente");

            // Act
            boolean resultado = evidenciaService.verificarHash(1L);

            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la evidencia no existe")
        void debeLanzarExcepcionCuandoEvidenciaNoExiste() {
            // Arrange
            when(evidenciaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.verificarHash(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar la evidencia cuando existe")
    void debeRetornarEvidenciaCuandoExiste() {
        // Arrange
        when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));

        // Act
        EvidenciaResponse resultado = evidenciaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.numeroItem()).isEqualTo("EV-001");
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todas las evidencias")
    void debeRetornarTodasLasEvidencias() {
        // Arrange
        when(evidenciaRepository.findAll()).thenReturn(List.of(evidenciaEjemplo));

        // Act
        List<EvidenciaResponse> resultado = evidenciaService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorEscena() debe filtrar evidencias por la escena indicada")
    void debeRetornarEvidenciasPorEscena() {
        // Arrange
        when(evidenciaRepository.findByEscenaId(1L)).thenReturn(List.of(evidenciaEjemplo));

        // Act
        List<EvidenciaResponse> resultado = evidenciaService.obtenerPorEscena(1L);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("actualizar() debe modificar tipo, descripción y número de item")
    void debeActualizarEvidenciaExistente() {
        // Arrange
        EvidenciaRequest request = new EvidenciaRequest(
                "EV-999", "Sangre", "Muestra de sangre", 1L, 1L, null);
        when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));
        when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvidenciaResponse resultado = evidenciaService.actualizar(1L, request);

        // Assert
        assertThat(resultado.tipo()).isEqualTo("Sangre");
        assertThat(resultado.numeroItem()).isEqualTo("EV-999");
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar la evidencia cuando existe")
        void debeEliminarEvidenciaExistente() {
            // Arrange
            when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));

            // Act
            evidenciaService.eliminar(1L);

            // Assert
            verify(evidenciaRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(evidenciaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(evidenciaRepository, never()).deleteById(any());
        }
    }

    @Test
    @DisplayName("asignarNumero() debe actualizar el número de item de la evidencia")
    void debeAsignarNumeroDeItem() {
        // Arrange
        when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));
        when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvidenciaResponse resultado = evidenciaService.asignarNumero(1L, "EV-777");

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
            when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(investigadorEjemplo));
            when(evidenciaRepository.save(any(Evidencia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EvidenciaResponse resultado = evidenciaService.firmarLevantamiento(1L, 1L);

            // Assert
            assertThat(resultado).isNotNull();
            verify(evidenciaRepository).save(evidenciaEjemplo);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> evidenciaService.firmarLevantamiento(1L, 77L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(evidenciaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("validarIntegridad()")
    class ValidarIntegridad {

        @Test
        @DisplayName("Debe retornar true cuando la evidencia tiene numeroItem, tipo y escena asignados")
        void debeRetornarTrueCuandoEvidenciaCompleta() {
            // Arrange
            when(evidenciaRepository.findById(1L)).thenReturn(Optional.of(evidenciaEjemplo));

            // Act
            boolean resultado = evidenciaService.validarIntegridad(1L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el número de item")
        void debeRetornarFalseCuandoFaltaNumeroItem() {
            // Arrange
            Evidencia incompleta = Evidencia.builder().id(2L).tipo("Arma").escena(escenaEjemplo).build();
            when(evidenciaRepository.findById(2L)).thenReturn(Optional.of(incompleta));

            // Act
            boolean resultado = evidenciaService.validarIntegridad(2L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }
}
