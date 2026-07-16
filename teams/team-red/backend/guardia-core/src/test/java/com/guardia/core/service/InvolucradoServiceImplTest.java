// Ruta destino: src/test/java/com/guardia/core/service/InvolucradoServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.response.InvolucradoResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Involucrado;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.repository.InvolucradoRepository;
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
 * Pruebas unitarias para {@link InvolucradoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvolucradoServiceImpl - Pruebas Unitarias")
class InvolucradoServiceImplTest {

    @Mock
    private InvolucradoRepository involucradoRepository;

    @InjectMocks
    private InvolucradoServiceImpl involucradoService;

    private Involucrado involucradoEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        involucradoEjemplo = Involucrado.builder()
                .id(1L)
                .nombre("Maria Lopez")
                .identificacion("V-9999999")
                .numeroTelefono("0414-1234567")
                .nacionalidad("Venezolana")
                .direccion("Calle Falsa 123")
                .rol(TipoRol.VICTIMA)
                .relacionConHecho("Víctima directa")
                .build();
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar el involucrado cuando existe")
    void debeRetornarInvolucradoCuandoExiste() {
        // Arrange
        when(involucradoRepository.findById(1L)).thenReturn(Optional.of(involucradoEjemplo));

        // Act
        InvolucradoResponse resultado = involucradoService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Maria Lopez");
        assertThat(resultado.rol()).isEqualTo(TipoRol.VICTIMA);
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(involucradoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> involucradoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Involucrado con id 99 no encontrado.");
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los involucrados mapeados")
    void debeRetornarTodosLosInvolucrados() {
        // Arrange
        when(involucradoRepository.findAll()).thenReturn(List.of(involucradoEjemplo));

        // Act
        List<InvolucradoResponse> resultado = involucradoService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).identificacion()).isEqualTo("V-9999999");
    }

    @Test
    @DisplayName("obtenerPorRol() debe filtrar involucrados por el rol indicado")
    void debeRetornarInvolucradosPorRol() {
        // Arrange
        when(involucradoRepository.findByRol(TipoRol.VICTIMA)).thenReturn(List.of(involucradoEjemplo));

        // Act
        List<InvolucradoResponse> resultado = involucradoService.obtenerPorRol(TipoRol.VICTIMA);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).rol()).isEqualTo(TipoRol.VICTIMA);
        verify(involucradoRepository).findByRol(TipoRol.VICTIMA);
    }

    @Test
    @DisplayName("obtenerPorRol() debe retornar lista vacía cuando no hay coincidencias")
    void debeRetornarListaVaciaCuandoNoHayCoincidenciasPorRol() {
        // Arrange
        when(involucradoRepository.findByRol(TipoRol.TESTIGO)).thenReturn(List.of());

        // Act
        List<InvolucradoResponse> resultado = involucradoService.obtenerPorRol(TipoRol.TESTIGO);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("obtenerPorExpediente() debe delegar en el repositorio filtrando por expedienteId")
    void debeRetornarInvolucradosPorExpediente() {
        // Arrange
        when(involucradoRepository.findByExpedienteId(5L)).thenReturn(List.of(involucradoEjemplo));

        // Act
        List<InvolucradoResponse> resultado = involucradoService.obtenerPorExpediente(5L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(involucradoRepository).findByExpedienteId(5L);
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el involucrado cuando existe")
        void debeEliminarInvolucradoExistente() {
            // Arrange
            when(involucradoRepository.findById(1L)).thenReturn(Optional.of(involucradoEjemplo));

            // Act
            involucradoService.eliminar(1L);

            // Assert
            verify(involucradoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(involucradoRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> involucradoService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(involucradoRepository, never()).deleteById(any());
        }
    }
}
