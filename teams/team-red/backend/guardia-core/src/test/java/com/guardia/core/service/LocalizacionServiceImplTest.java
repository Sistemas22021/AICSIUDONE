// Ruta destino: src/test/java/com/guardia/core/service/LocalizacionServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.LocalizacionRequest;
import com.guardia.core.dto.response.LocalizacionResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Localizacion;
import com.guardia.core.repository.LocalizacionRepository;
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
 * Pruebas unitarias para {@link LocalizacionServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LocalizacionServiceImpl - Pruebas Unitarias")
class LocalizacionServiceImplTest {

    @Mock
    private LocalizacionRepository localizacionRepository;

    @InjectMocks
    private LocalizacionServiceImpl localizacionService;

    private Localizacion localizacionEjemplo;
    private LocalizacionRequest requestEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        localizacionEjemplo = Localizacion.builder()
                .id(1L)
                .municipio("Libertador")
                .sector("Catia")
                .direccion("Av. Principal")
                .referencia("Cerca de la plaza")
                .latitud(10.5)
                .longitud(-66.9)
                .build();

        requestEjemplo = new LocalizacionRequest("Libertador", "Catia", "Av. Principal", "Cerca de la plaza", 10.5, -66.9);
    }

    @Test
    @DisplayName("crear() debe persistir y retornar la localización con resumen calculado")
    void debeCrearLocalizacionExitosamente() {
        // Arrange
        when(localizacionRepository.save(any(Localizacion.class))).thenReturn(localizacionEjemplo);

        // Act
        LocalizacionResponse resultado = localizacionService.crear(requestEjemplo);

        // Assert
        assertThat(resultado.municipio()).isEqualTo("Libertador");
        assertThat(resultado.resumen()).isEqualTo("Libertador, Catia - Av. Principal");
        verify(localizacionRepository).save(any(Localizacion.class));
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar la localización cuando existe")
    void debeRetornarLocalizacionCuandoExiste() {
        // Arrange
        when(localizacionRepository.findById(1L)).thenReturn(Optional.of(localizacionEjemplo));

        // Act
        LocalizacionResponse resultado = localizacionService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(localizacionRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> localizacionService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todas las localizaciones")
    void debeRetornarTodasLasLocalizaciones() {
        // Arrange
        when(localizacionRepository.findAll()).thenReturn(List.of(localizacionEjemplo));

        // Act
        List<LocalizacionResponse> resultado = localizacionService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar todos los campos de la localización existente")
        void debeActualizarLocalizacionExistente() {
            // Arrange
            LocalizacionRequest requestActualizado =
                    new LocalizacionRequest("Chacao", "Altamira", "Av. San Juan Bosco", "Torre XYZ", 10.49, -66.85);
            when(localizacionRepository.findById(1L)).thenReturn(Optional.of(localizacionEjemplo));
            when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            LocalizacionResponse resultado = localizacionService.actualizar(1L, requestActualizado);

            // Assert
            assertThat(resultado.municipio()).isEqualTo("Chacao");
            assertThat(resultado.sector()).isEqualTo("Altamira");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al actualizar una localización inexistente")
        void debeLanzarExcepcionAlActualizarInexistente() {
            // Arrange
            when(localizacionRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> localizacionService.actualizar(99L, requestEjemplo))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar la localización cuando existe")
        void debeEliminarLocalizacionExistente() {
            // Arrange
            when(localizacionRepository.findById(1L)).thenReturn(Optional.of(localizacionEjemplo));

            // Act
            localizacionService.eliminar(1L);

            // Assert
            verify(localizacionRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(localizacionRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> localizacionService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(localizacionRepository, never()).deleteById(any());
        }
    }

    @Test
    @DisplayName("registrarGPS() debe actualizar latitud y longitud")
    void debeRegistrarCoordenadasGPS() {
        // Arrange
        when(localizacionRepository.findById(1L)).thenReturn(Optional.of(localizacionEjemplo));
        when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        LocalizacionResponse resultado = localizacionService.registrarGPS(1L, 11.0, -67.0);

        // Assert
        assertThat(resultado.latitud()).isEqualTo(11.0);
        assertThat(resultado.longitud()).isEqualTo(-67.0);
    }

    @Test
    @DisplayName("registrarDireccionManual() debe actualizar los campos de dirección")
    void debeRegistrarDireccionManual() {
        // Arrange
        when(localizacionRepository.findById(1L)).thenReturn(Optional.of(localizacionEjemplo));
        when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        LocalizacionResponse resultado = localizacionService.registrarDireccionManual(
                1L, "Baruta", "Las Mercedes", "Calle 5", "Frente al parque");

        // Assert
        assertThat(resultado.municipio()).isEqualTo("Baruta");
        assertThat(resultado.direccion()).isEqualTo("Calle 5");
    }

    @Nested
    @DisplayName("validarUbicacion()")
    class ValidarUbicacion {

        @Test
        @DisplayName("Debe retornar true cuando existen coordenadas GPS válidas")
        void debeRetornarTrueConCoordenadasValidas() {
            // Arrange
            Localizacion localizacion = Localizacion.builder().id(2L).latitud(10.5).longitud(-66.9).build();
            when(localizacionRepository.findById(2L)).thenReturn(Optional.of(localizacion));

            // Act
            boolean resultado = localizacionService.validarUbicacion(2L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true cuando no hay GPS pero sí una dirección no vacía")
        void debeRetornarTrueConDireccionValida() {
            // Arrange
            Localizacion localizacion = Localizacion.builder().id(2L).direccion("Av. Principal").build();
            when(localizacionRepository.findById(2L)).thenReturn(Optional.of(localizacion));

            // Act
            boolean resultado = localizacionService.validarUbicacion(2L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando no hay coordenadas ni dirección")
        void debeRetornarFalseSinCoordenadasNiDireccion() {
            // Arrange
            Localizacion localizacion = Localizacion.builder().id(2L).build();
            when(localizacionRepository.findById(2L)).thenReturn(Optional.of(localizacion));

            // Act
            boolean resultado = localizacionService.validarUbicacion(2L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }
}
