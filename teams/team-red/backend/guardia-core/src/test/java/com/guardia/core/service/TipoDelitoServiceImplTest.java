// Ruta destino: src/test/java/com/guardia/core/service/TipoDelitoServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.TipoDelitoRequest;
import com.guardia.core.dto.response.TipoDelitoResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.SubtipoDelito;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.repository.TipoDelitoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link TipoDelitoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TipoDelitoServiceImpl - Pruebas Unitarias")
class TipoDelitoServiceImplTest {

    @Mock
    private TipoDelitoRepository tipoDelitoRepository;

    @InjectMocks
    private TipoDelitoServiceImpl tipoDelitoService;

    private TipoDelito tipoDelitoEjemplo;
    private TipoDelitoRequest requestEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        tipoDelitoEjemplo = TipoDelito.builder()
                .id(1L)
                .nombre("HOMICIDIO")
                .descripcion("Delito contra la vida")
                .requiereSubtipo(true)
                .subtipos(new ArrayList<>())
                .build();

        requestEjemplo = new TipoDelitoRequest("HOMICIDIO", "Delito contra la vida", true);
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear un tipo de delito cuando el nombre no existe previamente")
        void debeCrearTipoDelitoExitosamente() {
            // Arrange
            when(tipoDelitoRepository.existsByNombre("HOMICIDIO")).thenReturn(false);
            when(tipoDelitoRepository.save(any(TipoDelito.class))).thenReturn(tipoDelitoEjemplo);

            // Act
            TipoDelitoResponse resultado = tipoDelitoService.crear(requestEjemplo);

            // Assert
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nombre()).isEqualTo("HOMICIDIO");
            assertThat(resultado.requiereSubtipo()).isTrue();
            assertThat(resultado.subtipos()).isEmpty();
            verify(tipoDelitoRepository).save(any(TipoDelito.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando ya existe un tipo de delito con el mismo nombre")
        void debeLanzarExcepcionCuandoNombreYaExiste() {
            // Arrange
            when(tipoDelitoRepository.existsByNombre("HOMICIDIO")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> tipoDelitoService.crear(requestEjemplo))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Ya existe un tipo de delito con ese nombre.");
            verify(tipoDelitoRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe mapear correctamente los subtipos asociados")
    void debeMapearSubtiposAlObtenerPorId() {
        // Arrange
        SubtipoDelito subtipo = SubtipoDelito.builder()
                .id(10L).nombre("HOMICIDIO_CULPOSO").descripcion("desc").tipoDelito(tipoDelitoEjemplo).build();
        tipoDelitoEjemplo.setSubtipos(List.of(subtipo));
        when(tipoDelitoRepository.findById(1L)).thenReturn(Optional.of(tipoDelitoEjemplo));

        // Act
        TipoDelitoResponse resultado = tipoDelitoService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.subtipos()).hasSize(1);
        assertThat(resultado.subtipos().get(0).nombre()).isEqualTo("HOMICIDIO_CULPOSO");
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(tipoDelitoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tipoDelitoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("TipoDelito con id 99 no encontrado.");
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los tipos de delito mapeados")
    void debeRetornarTodosLosTiposDelito() {
        // Arrange
        when(tipoDelitoRepository.findAll()).thenReturn(List.of(tipoDelitoEjemplo));

        // Act
        List<TipoDelitoResponse> resultado = tipoDelitoService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("HOMICIDIO");
    }

    @Test
    @DisplayName("obtenerQueRequierenSubtipo() debe delegar en el repositorio con true")
    void debeObtenerTiposQueRequierenSubtipo() {
        // Arrange
        when(tipoDelitoRepository.findByRequiereSubtipo(true)).thenReturn(List.of(tipoDelitoEjemplo));

        // Act
        List<TipoDelitoResponse> resultado = tipoDelitoService.obtenerQueRequierenSubtipo();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(tipoDelitoRepository).findByRequiereSubtipo(true);
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar nombre, descripción y flag de subtipo")
        void debeActualizarTipoDelitoExistente() {
            // Arrange
            TipoDelitoRequest requestActualizado = new TipoDelitoRequest("ROBO", "Delito patrimonial", false);
            when(tipoDelitoRepository.findById(1L)).thenReturn(Optional.of(tipoDelitoEjemplo));
            when(tipoDelitoRepository.save(any(TipoDelito.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            TipoDelitoResponse resultado = tipoDelitoService.actualizar(1L, requestActualizado);

            // Assert
            assertThat(resultado.nombre()).isEqualTo("ROBO");
            assertThat(resultado.requiereSubtipo()).isFalse();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al actualizar un tipo inexistente")
        void debeLanzarExcepcionAlActualizarInexistente() {
            // Arrange
            when(tipoDelitoRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> tipoDelitoService.actualizar(99L, requestEjemplo))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(tipoDelitoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el tipo de delito cuando existe")
        void debeEliminarTipoDelitoExistente() {
            // Arrange
            when(tipoDelitoRepository.findById(1L)).thenReturn(Optional.of(tipoDelitoEjemplo));

            // Act
            tipoDelitoService.eliminar(1L);

            // Assert
            verify(tipoDelitoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no invocar deleteById cuando no existe")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(tipoDelitoRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> tipoDelitoService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(tipoDelitoRepository, never()).deleteById(anyLong());
        }
    }
}
