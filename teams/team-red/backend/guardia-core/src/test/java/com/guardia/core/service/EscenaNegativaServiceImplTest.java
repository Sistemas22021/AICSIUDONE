// Ruta destino: src/test/java/com/guardia/core/service/EscenaNegativaServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.EscenaNegativaRequest;
import com.guardia.core.dto.response.EscenaNegativaResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.EscenaNegativa;
import com.guardia.core.repository.EscenaNegativaRepository;
import com.guardia.core.repository.EscenaRepository;
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
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link EscenaNegativaServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EscenaNegativaServiceImpl - Pruebas Unitarias")
class EscenaNegativaServiceImplTest {

    @Mock
    private EscenaNegativaRepository escenaNegativaRepository;

    @Mock
    private EscenaRepository escenaRepository;

    @InjectMocks
    private EscenaNegativaServiceImpl escenaNegativaService;

    private Escena escenaEjemplo;
    private EscenaNegativa escenaNegativaEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        escenaEjemplo = Escena.builder().id(1L).build();
        escenaNegativaEjemplo = EscenaNegativa.builder()
                .id(1L)
                .elementoBuscado("Arma de fuego")
                .areaInspeccionada("Habitación principal")
                .resultado("NO_ENCONTRADO")
                .observacion("Sin hallazgos")
                .escena(escenaEjemplo)
                .sinElementosNegativos(false)
                .build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear un registro con datos detallados cuando sinElementosNegativos es false")
        void debeCrearRegistroDetallado() {
            // Arrange
            EscenaNegativaRequest request = new EscenaNegativaRequest(
                    "Arma de fuego", "Habitación principal", "NO_ENCONTRADO", "Sin hallazgos", 1L, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaNegativaRepository.save(any(EscenaNegativa.class))).thenReturn(escenaNegativaEjemplo);

            // Act
            EscenaNegativaResponse resultado = escenaNegativaService.crear(request);

            // Assert
            assertThat(resultado.elementoBuscado()).isEqualTo("Arma de fuego");
            assertThat(resultado.sinElementosNegativos()).isFalse();
        }

        @Test
        @DisplayName("Debe marcar el registro como 'sin elementos negativos' cuando el flag es true")
        void debeMarcarSinElementosNegativos() {
            // Arrange
            EscenaNegativaRequest request = new EscenaNegativaRequest(
                    null, null, null, null, 1L, true);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaNegativaRepository.save(any(EscenaNegativa.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaNegativaResponse resultado = escenaNegativaService.crear(request);

            // Assert
            assertThat(resultado.sinElementosNegativos()).isTrue();
            assertThat(resultado.elementoBuscado()).isEqualTo("SIN_ELEMENTOS_NEGATIVOS");
            assertThat(resultado.resultado()).isEqualTo("SIN_HALLAZGOS");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la escena no existe")
        void debeLanzarExcepcionCuandoEscenaNoExiste() {
            // Arrange
            EscenaNegativaRequest request = new EscenaNegativaRequest(
                    "Arma", "Cocina", "NO_ENCONTRADO", null, 99L, false);
            when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaNegativaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Escena con id 99 no encontrado.");
            verify(escenaNegativaRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar el registro cuando existe")
    void debeRetornarRegistroCuandoExiste() {
        // Arrange
        when(escenaNegativaRepository.findById(1L)).thenReturn(Optional.of(escenaNegativaEjemplo));

        // Act
        EscenaNegativaResponse resultado = escenaNegativaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(escenaNegativaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> escenaNegativaService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los registros")
    void debeRetornarTodosLosRegistros() {
        // Arrange
        when(escenaNegativaRepository.findAll()).thenReturn(List.of(escenaNegativaEjemplo));

        // Act
        List<EscenaNegativaResponse> resultado = escenaNegativaService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorEscena() debe filtrar registros por la escena indicada")
    void debeRetornarRegistrosPorEscena() {
        // Arrange
        when(escenaNegativaRepository.findByEscenaId(1L)).thenReturn(List.of(escenaNegativaEjemplo));

        // Act
        List<EscenaNegativaResponse> resultado = escenaNegativaService.obtenerPorEscena(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(escenaNegativaRepository).findByEscenaId(1L);
    }

    @Test
    @DisplayName("actualizar() debe modificar los campos del registro existente")
    void debeActualizarRegistroExistente() {
        // Arrange
        EscenaNegativaRequest request = new EscenaNegativaRequest(
                "Cuchillo", "Cocina", "ENCONTRADO", "Hallazgo relevante", 1L, false);
        when(escenaNegativaRepository.findById(1L)).thenReturn(Optional.of(escenaNegativaEjemplo));
        when(escenaNegativaRepository.save(any(EscenaNegativa.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EscenaNegativaResponse resultado = escenaNegativaService.actualizar(1L, request);

        // Assert
        assertThat(resultado.elementoBuscado()).isEqualTo("Cuchillo");
        assertThat(resultado.resultado()).isEqualTo("ENCONTRADO");
    }

    @Test
    @DisplayName("eliminar() siempre debe lanzar BusinessException, incluso si el registro existe")
    void eliminarSiempreDebeLanzarBusinessException() {
        // Arrange - no se requiere estado previo: la regla de negocio prohíbe eliminar siempre

        // Act & Assert
        assertThatThrownBy(() -> escenaNegativaService.eliminar(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Los registros de escena negativa no pueden eliminarse una vez guardados.");
        verifyNoInteractions(escenaNegativaRepository);
    }

    @Test
    @DisplayName("registrarResultadoNoEncontrado() debe actualizar área, observación y resultado")
    void debeRegistrarResultadoNoEncontrado() {
        // Arrange
        when(escenaNegativaRepository.findById(1L)).thenReturn(Optional.of(escenaNegativaEjemplo));
        when(escenaNegativaRepository.save(any(EscenaNegativa.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EscenaNegativaResponse resultado = escenaNegativaService.registrarResultadoNoEncontrado(
                1L, "Sótano", "Área revisada completamente");

        // Assert
        assertThat(resultado.areaInspeccionada()).isEqualTo("Sótano");
        assertThat(resultado.resultado()).isEqualTo("NO_ENCONTRADO");
    }

    @Test
    @DisplayName("agregarObservacion() debe concatenar con el separador cuando ya existe una observación")
    void debeConcatenarObservacionExistente() {
        // Arrange
        when(escenaNegativaRepository.findById(1L)).thenReturn(Optional.of(escenaNegativaEjemplo));
        when(escenaNegativaRepository.save(any(EscenaNegativa.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EscenaNegativaResponse resultado = escenaNegativaService.agregarObservacion(1L, "Observación adicional");

        // Assert
        assertThat(resultado.observacion()).isEqualTo("Sin hallazgos | Observación adicional");
    }

    @Nested
    @DisplayName("validarRegistro()")
    class ValidarRegistro {

        @Test
        @DisplayName("Debe retornar true cuando el elemento buscado y el área están completos")
        void debeRetornarTrueCuandoRegistroCompleto() {
            // Arrange
            when(escenaNegativaRepository.findById(1L)).thenReturn(Optional.of(escenaNegativaEjemplo));

            // Act
            boolean resultado = escenaNegativaService.validarRegistro(1L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el área inspeccionada")
        void debeRetornarFalseCuandoFaltaArea() {
            // Arrange
            EscenaNegativa incompleto = EscenaNegativa.builder()
                    .id(2L).elementoBuscado("Arma").areaInspeccionada(null).build();
            when(escenaNegativaRepository.findById(2L)).thenReturn(Optional.of(incompleto));

            // Act
            boolean resultado = escenaNegativaService.validarRegistro(2L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }
}
