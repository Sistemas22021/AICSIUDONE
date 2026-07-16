// Ruta destino: src/test/java/com/guardia/core/service/PropuestaModusOperandiServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.AprobarPropuestaMoRequest;
import com.guardia.core.dto.request.CorregirPropuestaMoRequest;
import com.guardia.core.dto.request.RechazarPropuestaMoRequest;
import com.guardia.core.dto.response.PropuestaModusOperandiResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import com.guardia.core.repository.UsuarioRepository;
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
 * Pruebas unitarias para {@link PropuestaModusOperandiServiceImpl} (HU3 -
 * "Validación experta del MO propuesto").
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PropuestaModusOperandiServiceImpl - Pruebas Unitarias")
class PropuestaModusOperandiServiceImplTest {

    @Mock private PropuestaModusOperandiRepository propuestaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PropuestaModusOperandiServiceImpl propuestaService;

    private Expediente expedienteEjemplo;
    private Usuario analistaEjemplo;
    private PropuestaModusOperandi propuestaEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        expedienteEjemplo = Expediente.builder().id(1L).folio("EXP-2026-AAAA1111").build();
        analistaEjemplo = Usuario.builder().id(1L).nombre("Analista Ruiz").identificacion("V-1").correo("r@x.com").build();
        propuestaEjemplo = PropuestaModusOperandi.builder()
                .id(1L)
                .expediente(expedienteEjemplo)
                .version(1)
                .vigente(true)
                .estado(EstadoPropuestaMO.PENDIENTE)
                .caracteristicasComunes("Ingreso por ventana trasera")
                .expedientesSimilares(new ArrayList<>())
                .revisadoPorExperto(false)
                .build();
    }

    @Nested
    @DisplayName("obtenerVigentePorExpediente()")
    class ObtenerVigentePorExpediente {

        @Test
        @DisplayName("Debe retornar la propuesta vigente cuando existe")
        void debeRetornarPropuestaVigente() {
            // Arrange
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.of(propuestaEjemplo));

            // Act
            PropuestaModusOperandiResponse resultado = propuestaService.obtenerVigentePorExpediente(1L);

            // Assert
            assertThat(resultado.vigente()).isTrue();
            assertThat(resultado.folioExpediente()).isEqualTo("EXP-2026-AAAA1111");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando no hay propuesta vigente")
        void debeLanzarExcepcionCuandoNoHayVigente() {
            // Arrange
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> propuestaService.obtenerVigentePorExpediente(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("historialPorExpediente() debe retornar todas las versiones ordenadas descendentemente")
    void debeRetornarHistorialDeVersiones() {
        // Arrange
        when(propuestaRepository.findByExpedienteIdOrderByVersionDesc(1L)).thenReturn(List.of(propuestaEjemplo));

        // Act
        List<PropuestaModusOperandiResponse> resultado = propuestaService.historialPorExpediente(1L);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Nested
    @DisplayName("aprobar()")
    class Aprobar {

        @Test
        @DisplayName("Debe aprobar la propuesta y marcarla como revisada por el analista")
        void debeAprobarPropuestaExitosamente() {
            // Arrange
            AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(1L);
            when(propuestaRepository.findById(1L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(analistaEjemplo));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            PropuestaModusOperandiResponse resultado = propuestaService.aprobar(1L, request);

            // Assert
            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.APROBADA);
            assertThat(resultado.revisadoPorExperto()).isTrue();
            assertThat(resultado.analistaRevisorId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la propuesta no existe")
        void debeLanzarExcepcionCuandoPropuestaNoExiste() {
            // Arrange
            AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(1L);
            when(propuestaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> propuestaService.aprobar(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(usuarioRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el analista no existe")
        void debeLanzarExcepcionCuandoAnalistaNoExiste() {
            // Arrange
            AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(77L);
            when(propuestaRepository.findById(1L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> propuestaService.aprobar(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(propuestaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("corregir()")
    class Corregir {

        @Test
        @DisplayName("Debe sobreescribir únicamente los campos de clasificación enviados")
        void debeCorregirSoloLosCamposEnviados() {
            // Arrange: solo se envía posibleFirma; caracteristicasComunes y consistenciaHorarioZona quedan null
            CorregirPropuestaMoRequest request = new CorregirPropuestaMoRequest(
                    1L, null, "Firma distintiva: forzado de cerraduras", null, "Corrección tras revisión de campo");
            when(propuestaRepository.findById(1L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(analistaEjemplo));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            PropuestaModusOperandiResponse resultado = propuestaService.corregir(1L, request);

            // Assert
            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.CORREGIDA);
            assertThat(resultado.posibleFirma()).isEqualTo("Firma distintiva: forzado de cerraduras");
            // El valor original se conserva porque el campo no vino informado en la solicitud
            assertThat(resultado.caracteristicasComunes()).isEqualTo("Ingreso por ventana trasera");
            assertThat(resultado.justificacionRevision()).isEqualTo("Corrección tras revisión de campo");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la propuesta no existe")
        void debeLanzarExcepcionCuandoPropuestaNoExiste() {
            // Arrange
            CorregirPropuestaMoRequest request = new CorregirPropuestaMoRequest(1L, "a", "b", "c", "justificación");
            when(propuestaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> propuestaService.corregir(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("rechazar()")
    class Rechazar {

        @Test
        @DisplayName("Debe rechazar la propuesta y registrar la clasificación manual")
        void debeRechazarPropuestaExitosamente() {
            // Arrange
            RechazarPropuestaMoRequest request = new RechazarPropuestaMoRequest(
                    1L, "Hurto simple sin patrón asociado", "La IA sobreestimó la similitud");
            when(propuestaRepository.findById(1L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(analistaEjemplo));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            PropuestaModusOperandiResponse resultado = propuestaService.rechazar(1L, request);

            // Assert
            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.RECHAZADA);
            assertThat(resultado.clasificacionManual()).isEqualTo("Hurto simple sin patrón asociado");
            assertThat(resultado.justificacionRevision()).isEqualTo("La IA sobreestimó la similitud");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el analista no existe")
        void debeLanzarExcepcionCuandoAnalistaNoExiste() {
            // Arrange
            RechazarPropuestaMoRequest request = new RechazarPropuestaMoRequest(77L, "manual", "justificación");
            when(propuestaRepository.findById(1L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> propuestaService.rechazar(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("toResponse() debe lanzar BusinessException cuando la propuesta no tiene expediente asociado")
    void debeLanzarExcepcionCuandoPropuestaSinExpediente() {
        // Arrange: propuesta huérfana, sin expediente (caso de datos corruptos/edge case)
        PropuestaModusOperandi huerfana = PropuestaModusOperandi.builder()
                .id(2L).expediente(null).expedientesSimilares(new ArrayList<>()).build();
        when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.of(huerfana));

        // Act & Assert
        assertThatThrownBy(() -> propuestaService.obtenerVigentePorExpediente(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no tiene expediente asociado");
    }
}
