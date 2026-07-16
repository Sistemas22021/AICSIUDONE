// Ruta destino: src/test/java/com/guardia/core/service/SubtipoDelitoServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.SubtipoDelitoRequest;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.SubtipoDelito;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.repository.SubtipoDelitoRepository;
import com.guardia.core.repository.TipoDelitoRepository;
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
 * Pruebas unitarias para {@link SubtipoDelitoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubtipoDelitoServiceImpl - Pruebas Unitarias")
class SubtipoDelitoServiceImplTest {

    @Mock
    private SubtipoDelitoRepository subtipoDelitoRepository;

    @Mock
    private TipoDelitoRepository tipoDelitoRepository;

    @InjectMocks
    private SubtipoDelitoServiceImpl subtipoDelitoService;

    private TipoDelito tipoDelito;
    private SubtipoDelito subtipoEjemplo;
    private SubtipoDelitoRequest requestEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        tipoDelito = TipoDelito.builder().id(1L).nombre("HOMICIDIO").requiereSubtipo(true).build();
        subtipoEjemplo = SubtipoDelito.builder()
                .id(10L).nombre("HOMICIDIO_CULPOSO").descripcion("desc").tipoDelito(tipoDelito).build();
        requestEjemplo = new SubtipoDelitoRequest("HOMICIDIO_CULPOSO", "desc", 1L);
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear un subtipo cuando no existe combinación nombre+tipo y el tipo existe")
        void debeCrearSubtipoExitosamente() {
            // Arrange
            when(subtipoDelitoRepository.existsByNombreAndTipoDelitoId("HOMICIDIO_CULPOSO", 1L)).thenReturn(false);
            when(tipoDelitoRepository.findById(1L)).thenReturn(Optional.of(tipoDelito));
            when(subtipoDelitoRepository.save(any(SubtipoDelito.class))).thenReturn(subtipoEjemplo);

            // Act
            SubtipoDelitoResponse resultado = subtipoDelitoService.crear(requestEjemplo);

            // Assert
            assertThat(resultado.id()).isEqualTo(10L);
            assertThat(resultado.nombre()).isEqualTo("HOMICIDIO_CULPOSO");
            assertThat(resultado.tipoDelitoId()).isEqualTo(1L);
            assertThat(resultado.tipoDelitoNombre()).isEqualTo("HOMICIDIO");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando ya existe el subtipo para ese tipo de delito")
        void debeLanzarExcepcionCuandoSubtipoYaExiste() {
            // Arrange
            when(subtipoDelitoRepository.existsByNombreAndTipoDelitoId("HOMICIDIO_CULPOSO", 1L)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> subtipoDelitoService.crear(requestEjemplo))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Ya existe ese subtipo para el tipo de delito indicado.");

            verify(tipoDelitoRepository, never()).findById(any());
            verify(subtipoDelitoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el tipo de delito padre no existe")
        void debeLanzarExcepcionCuandoTipoDelitoNoExiste() {
            // Arrange
            when(subtipoDelitoRepository.existsByNombreAndTipoDelitoId("HOMICIDIO_CULPOSO", 1L)).thenReturn(false);
            when(tipoDelitoRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> subtipoDelitoService.crear(requestEjemplo))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("TipoDelito con id 1 no encontrado.");
            verify(subtipoDelitoRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar el subtipo cuando existe")
    void debeRetornarSubtipoCuandoExiste() {
        // Arrange
        when(subtipoDelitoRepository.findById(10L)).thenReturn(Optional.of(subtipoEjemplo));

        // Act
        SubtipoDelitoResponse resultado = subtipoDelitoService.obtenerPorId(10L);

        // Assert
        assertThat(resultado.nombre()).isEqualTo("HOMICIDIO_CULPOSO");
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(subtipoDelitoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> subtipoDelitoService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los subtipos")
    void debeRetornarTodosLosSubtipos() {
        // Arrange
        when(subtipoDelitoRepository.findAll()).thenReturn(List.of(subtipoEjemplo));

        // Act
        List<SubtipoDelitoResponse> resultado = subtipoDelitoService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorTipoDelito() debe delegar en el repositorio filtrando por tipo padre")
    void debeRetornarSubtiposPorTipoDelito() {
        // Arrange
        when(subtipoDelitoRepository.findByTipoDelitoId(1L)).thenReturn(List.of(subtipoEjemplo));

        // Act
        List<SubtipoDelitoResponse> resultado = subtipoDelitoService.obtenerPorTipoDelito(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(subtipoDelitoRepository).findByTipoDelitoId(1L);
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar nombre y descripción del subtipo existente")
        void debeActualizarSubtipoExistente() {
            // Arrange
            SubtipoDelitoRequest requestActualizado = new SubtipoDelitoRequest("NUEVO_NOMBRE", "nueva desc", 1L);
            when(subtipoDelitoRepository.findById(10L)).thenReturn(Optional.of(subtipoEjemplo));
            when(subtipoDelitoRepository.save(any(SubtipoDelito.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            SubtipoDelitoResponse resultado = subtipoDelitoService.actualizar(10L, requestActualizado);

            // Assert
            assertThat(resultado.nombre()).isEqualTo("NUEVO_NOMBRE");
            assertThat(resultado.descripcion()).isEqualTo("nueva desc");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al actualizar un subtipo inexistente")
        void debeLanzarExcepcionAlActualizarInexistente() {
            // Arrange
            when(subtipoDelitoRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> subtipoDelitoService.actualizar(999L, requestEjemplo))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el subtipo cuando existe")
        void debeEliminarSubtipoExistente() {
            // Arrange
            when(subtipoDelitoRepository.findById(10L)).thenReturn(Optional.of(subtipoEjemplo));

            // Act
            subtipoDelitoService.eliminar(10L);

            // Assert
            verify(subtipoDelitoRepository).deleteById(10L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el subtipo a eliminar no existe")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(subtipoDelitoRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> subtipoDelitoService.eliminar(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(subtipoDelitoRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("validarCorrespondencia()")
    class ValidarCorrespondencia {

        @Test
        @DisplayName("Debe retornar true cuando el subtipo pertenece al tipo indicado")
        void debeRetornarTrueCuandoCorresponde() {
            // Arrange
            when(subtipoDelitoRepository.findById(10L)).thenReturn(Optional.of(subtipoEjemplo));

            // Act
            boolean resultado = subtipoDelitoService.validarCorrespondencia(10L, 1L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando el subtipo pertenece a otro tipo distinto")
        void debeRetornarFalseCuandoNoCorresponde() {
            // Arrange
            when(subtipoDelitoRepository.findById(10L)).thenReturn(Optional.of(subtipoEjemplo));

            // Act
            boolean resultado = subtipoDelitoService.validarCorrespondencia(10L, 99L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }
}
