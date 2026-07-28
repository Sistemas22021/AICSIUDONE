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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link PropuestaModusOperandiServiceImpl} (HU3 - validación experta del MO).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PropuestaModusOperandiServiceImpl - Pruebas Unitarias")
class PropuestaModusOperandiServiceImplTest {

    @Mock private PropuestaModusOperandiRepository propuestaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PropuestaModusOperandiServiceImpl propuestaService;

    private UUID analistaId;
    private Usuario analistaEjemplo;
    private Expediente expedienteEjemplo;
    private PropuestaModusOperandi propuestaEjemplo;

    @BeforeEach
    void setUp() {
        analistaId = UUID.randomUUID();
        analistaEjemplo = Usuario.builder()
                .id(analistaId).username("aruiz").password("hash").fullName("Analista Ruiz").rol("ANALISTA").build();
        expedienteEjemplo = Expediente.builder().id(1L).folio("EXP-2026-AAAA1111").build();
        propuestaEjemplo = PropuestaModusOperandi.builder()
                .id(50L).expediente(expedienteEjemplo).version(1).vigente(true)
                .estado(EstadoPropuestaMO.PENDIENTE).revisadoPorExperto(false)
                .expedientesSimilares(List.of())
                .build();
    }

    @Nested
    @DisplayName("obtenerVigentePorExpediente()")
    class ObtenerVigentePorExpediente {

        @Test
        @DisplayName("Debe retornar la propuesta vigente cuando existe")
        void debeRetornarPropuestaVigente() {
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.of(propuestaEjemplo));

            PropuestaModusOperandiResponse resultado = propuestaService.obtenerVigentePorExpediente(1L);

            assertThat(resultado.id()).isEqualTo(50L);
            assertThat(resultado.folioExpediente()).isEqualTo("EXP-2026-AAAA1111");
            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.PENDIENTE);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando no hay propuesta vigente")
        void debeLanzarExcepcionCuandoNoHayVigente() {
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propuestaService.obtenerVigentePorExpediente(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la propuesta no tiene expediente asociado")
        void debeLanzarExcepcionCuandoSinExpediente() {
            PropuestaModusOperandi huerfana = PropuestaModusOperandi.builder()
                    .id(60L).expediente(null).version(1).estado(EstadoPropuestaMO.PENDIENTE).build();
            when(propuestaRepository.findByExpedienteIdAndVigenteTrue(1L)).thenReturn(Optional.of(huerfana));

            assertThatThrownBy(() -> propuestaService.obtenerVigentePorExpediente(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("no tiene expediente asociado");
        }
    }

    @Test
    @DisplayName("historialPorExpediente() debe retornar el historial ordenado por versión descendente")
    void debeRetornarHistorial() {
        PropuestaModusOperandi v2 = PropuestaModusOperandi.builder()
                .id(51L).expediente(expedienteEjemplo).version(2).estado(EstadoPropuestaMO.PENDIENTE)
                .expedientesSimilares(List.of()).build();
        when(propuestaRepository.findByExpedienteIdOrderByVersionDesc(1L)).thenReturn(List.of(v2, propuestaEjemplo));

        List<PropuestaModusOperandiResponse> resultado = propuestaService.historialPorExpediente(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).version()).isEqualTo(2);
    }

    @Nested
    @DisplayName("aprobar()")
    class Aprobar {

        @Test
        @DisplayName("Debe aprobar la propuesta y marcarla como revisada por experto")
        void debeAprobarExitosamente() {
            AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(analistaId);
            when(propuestaRepository.findById(50L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(analistaId)).thenReturn(Optional.of(analistaEjemplo));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            PropuestaModusOperandiResponse resultado = propuestaService.aprobar(50L, request);

            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.APROBADA);
            assertThat(resultado.revisadoPorExperto()).isTrue();
            assertThat(resultado.analistaRevisorNombre()).isEqualTo("Analista Ruiz");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la propuesta no existe")
        void debeLanzarExcepcionCuandoPropuestaNoExiste() {
            AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(analistaId);
            when(propuestaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propuestaService.aprobar(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verifyNoInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el analista no existe")
        void debeLanzarExcepcionCuandoAnalistaNoExiste() {
            UUID inexistente = UUID.randomUUID();
            AprobarPropuestaMoRequest request = new AprobarPropuestaMoRequest(inexistente);
            when(propuestaRepository.findById(50L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propuestaService.aprobar(50L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(propuestaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("corregir()")
    class Corregir {

        @Test
        @DisplayName("Debe sobreescribir solo los campos de clasificación enviados distintos de null")
        void debeCorregirCamposEnviados() {
            propuestaEjemplo.setCaracteristicasComunes("original");
            propuestaEjemplo.setPosibleFirma("firma original");
            CorregirPropuestaMoRequest request = new CorregirPropuestaMoRequest(
                    analistaId, "nuevas características", null, "zona consistente", "Justificación obligatoria");
            when(propuestaRepository.findById(50L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(analistaId)).thenReturn(Optional.of(analistaEjemplo));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            PropuestaModusOperandiResponse resultado = propuestaService.corregir(50L, request);

            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.CORREGIDA);
            assertThat(resultado.caracteristicasComunes()).isEqualTo("nuevas características");
            assertThat(resultado.posibleFirma()).isEqualTo("firma original"); // no enviado -> se conserva
            assertThat(resultado.consistenciaHorarioZona()).isEqualTo("zona consistente");
            assertThat(resultado.justificacionRevision()).isEqualTo("Justificación obligatoria");
            assertThat(resultado.revisadoPorExperto()).isTrue();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la propuesta no existe")
        void debeLanzarExcepcionCuandoPropuestaNoExiste() {
            CorregirPropuestaMoRequest request = new CorregirPropuestaMoRequest(
                    analistaId, "x", null, null, "Justificación");
            when(propuestaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propuestaService.corregir(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("rechazar()")
    class Rechazar {

        @Test
        @DisplayName("Debe rechazar la propuesta y registrar la clasificación manual")
        void debeRechazarExitosamente() {
            RechazarPropuestaMoRequest request = new RechazarPropuestaMoRequest(
                    analistaId, "ROBO_SIMPLE", "El patrón no coincide con los casos previos");
            when(propuestaRepository.findById(50L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(analistaId)).thenReturn(Optional.of(analistaEjemplo));
            when(propuestaRepository.save(any(PropuestaModusOperandi.class))).thenAnswer(inv -> inv.getArgument(0));

            PropuestaModusOperandiResponse resultado = propuestaService.rechazar(50L, request);

            assertThat(resultado.estado()).isEqualTo(EstadoPropuestaMO.RECHAZADA);
            assertThat(resultado.clasificacionManual()).isEqualTo("ROBO_SIMPLE");
            assertThat(resultado.justificacionRevision()).isEqualTo("El patrón no coincide con los casos previos");
            assertThat(resultado.revisadoPorExperto()).isTrue();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el analista no existe")
        void debeLanzarExcepcionCuandoAnalistaNoExiste() {
            UUID inexistente = UUID.randomUUID();
            RechazarPropuestaMoRequest request = new RechazarPropuestaMoRequest(inexistente, "X", "Justificación");
            when(propuestaRepository.findById(50L)).thenReturn(Optional.of(propuestaEjemplo));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propuestaService.rechazar(50L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(propuestaRepository, never()).save(any());
        }
    }
}
