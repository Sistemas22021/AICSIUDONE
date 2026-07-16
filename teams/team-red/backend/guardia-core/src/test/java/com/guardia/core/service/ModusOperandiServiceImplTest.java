// Ruta destino: src/test/java/com/guardia/core/service/ModusOperandiServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.ModusOperandiRequest;
import com.guardia.core.dto.response.ModusOperandiResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.ModusOperandi;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.ModusOperandiRepository;
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
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link ModusOperandiServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ModusOperandiServiceImpl - Pruebas Unitarias")
class ModusOperandiServiceImplTest {

    @Mock
    private ModusOperandiRepository modusOperandiRepository;

    @Mock
    private ExpedienteRepository expedienteRepository;

    @InjectMocks
    private ModusOperandiServiceImpl modusOperandiService;

    private ModusOperandi modusEjemplo;
    private Expediente expedienteEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        modusEjemplo = ModusOperandi.builder()
                .id(1L)
                .descripcionAnalitica("Robo con arma blanca en horario nocturno")
                .patronDetectado("ROBO_NOCTURNO")
                .nivelConfianza("ALTO")
                .expedientes(new ArrayList<>())
                .build();

        expedienteEjemplo = Expediente.builder().id(10L).folio("EXP-2026-AAAA1111").build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear un modus operandi con lista de expedientes vacía")
        void debeCrearModusOperandiExitosamente() {
            // Arrange
            ModusOperandiRequest request = new ModusOperandiRequest(
                    "Robo con arma blanca en horario nocturno", "ROBO_NOCTURNO", "ALTO");
            when(modusOperandiRepository.save(any(ModusOperandi.class))).thenReturn(modusEjemplo);

            // Act
            ModusOperandiResponse resultado = modusOperandiService.crear(request);

            // Assert
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.patronDetectado()).isEqualTo("ROBO_NOCTURNO");
            assertThat(resultado.expedienteIds()).isEmpty();
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar el modus operandi cuando existe")
    void debeRetornarModusOperandiCuandoExiste() {
        // Arrange
        when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));

        // Act
        ModusOperandiResponse resultado = modusOperandiService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.nivelConfianza()).isEqualTo("ALTO");
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(modusOperandiRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> modusOperandiService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los modus operandi registrados")
    void debeRetornarTodosLosModusOperandi() {
        // Arrange
        when(modusOperandiRepository.findAll()).thenReturn(List.of(modusEjemplo));

        // Act
        List<ModusOperandiResponse> resultado = modusOperandiService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("buscarPorPatron() debe delegar la búsqueda insensible a mayúsculas en el repositorio")
    void debeBuscarPorPatron() {
        // Arrange
        when(modusOperandiRepository.findByPatronDetectadoContainingIgnoreCase("robo"))
                .thenReturn(List.of(modusEjemplo));

        // Act
        List<ModusOperandiResponse> resultado = modusOperandiService.buscarPorPatron("robo");

        // Assert
        assertThat(resultado).hasSize(1);
        verify(modusOperandiRepository).findByPatronDetectadoContainingIgnoreCase("robo");
    }

    @Test
    @DisplayName("actualizar() debe modificar descripción, patrón y nivel de confianza")
    void debeActualizarModusOperandiExistente() {
        // Arrange
        ModusOperandiRequest request = new ModusOperandiRequest("Nueva descripción", "PATRON_X", "MEDIO");
        when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
        when(modusOperandiRepository.save(any(ModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ModusOperandiResponse resultado = modusOperandiService.actualizar(1L, request);

        // Assert
        assertThat(resultado.descripcionAnalitica()).isEqualTo("Nueva descripción");
        assertThat(resultado.nivelConfianza()).isEqualTo("MEDIO");
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el modus operandi cuando existe")
        void debeEliminarModusOperandiExistente() {
            // Arrange
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));

            // Act
            modusOperandiService.eliminar(1L);

            // Assert
            verify(modusOperandiRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al eliminar uno inexistente")
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(modusOperandiRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> modusOperandiService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(modusOperandiRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("vincularExpediente()")
    class VincularExpediente {

        @Test
        @DisplayName("Debe vincular el expediente cuando no estaba vinculado previamente")
        void debeVincularExpedienteExitosamente() {
            // Arrange
            expedienteEjemplo.setModusOperandiList(new ArrayList<>());
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteEjemplo));
            when(modusOperandiRepository.save(any(ModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ModusOperandiResponse resultado = modusOperandiService.vincularExpediente(1L, 10L);

            // Assert
            assertThat(resultado.expedienteIds()).containsExactly(10L);
            assertThat(expedienteEjemplo.getModusOperandiList()).contains(modusEjemplo);
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el expediente ya está vinculado")
        void debeLanzarExcepcionCuandoYaVinculado() {
            // Arrange
            modusEjemplo.getExpedientes().add(expedienteEjemplo);
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> modusOperandiService.vincularExpediente(1L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("El expediente ya está vinculado a este modus operandi.");
            verify(modusOperandiRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el expediente no existe")
        void debeLanzarExcepcionCuandoExpedienteNoExiste() {
            // Arrange
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
            when(expedienteRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> modusOperandiService.vincularExpediente(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("desvincularExpediente() debe remover el expediente de la lista")
    void debeDesvincularExpediente() {
        // Arrange
        modusEjemplo.getExpedientes().add(expedienteEjemplo);
        when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
        when(modusOperandiRepository.save(any(ModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ModusOperandiResponse resultado = modusOperandiService.desvincularExpediente(1L, 10L);

        // Assert
        assertThat(resultado.expedienteIds()).doesNotContain(10L);
    }

    @Test
    @DisplayName("agregarPatron() debe actualizar el patrón detectado")
    void debeAgregarPatron() {
        // Arrange
        when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
        when(modusOperandiRepository.save(any(ModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ModusOperandiResponse resultado = modusOperandiService.agregarPatron(1L, "NUEVO_PATRON");

        // Assert
        assertThat(resultado.patronDetectado()).isEqualTo("NUEVO_PATRON");
    }

    @Nested
    @DisplayName("compararExpedientes()")
    class CompararExpedientes {

        @Test
        @DisplayName("Debe delegar la comparación en la entidad y retornar su resultado")
        void debeCompararExpedientesExitosamente() {
            // Arrange
            Expediente expedienteB = Expediente.builder().id(20L).folio("EXP-2026-BBBB2222").build();
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteEjemplo));
            when(expedienteRepository.findById(20L)).thenReturn(Optional.of(expedienteB));

            // Act
            double resultado = modusOperandiService.compararExpedientes(1L, 10L, 20L);

            // Assert: la lógica de comparación en el modelo actual retorna 0.0 (pendiente de implementar)
            assertThat(resultado).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el primer expediente no existe")
        void debeLanzarExcepcionCuandoExpedienteANoExiste() {
            // Arrange
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> modusOperandiService.compararExpedientes(1L, 10L, 20L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(expedienteRepository, never()).findById(20L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el segundo expediente no existe")
        void debeLanzarExcepcionCuandoExpedienteBNoExiste() {
            // Arrange
            when(modusOperandiRepository.findById(1L)).thenReturn(Optional.of(modusEjemplo));
            when(expedienteRepository.findById(10L)).thenReturn(Optional.of(expedienteEjemplo));
            when(expedienteRepository.findById(20L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> modusOperandiService.compararExpedientes(1L, 10L, 20L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
