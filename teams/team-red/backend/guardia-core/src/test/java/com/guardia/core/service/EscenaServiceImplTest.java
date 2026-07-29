// Ruta destino: src/test/java/com/guardia/core/service/EscenaServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.HashStrategy;
import com.guardia.core.dto.request.EscenaRequest;
import com.guardia.core.dto.request.LiberarEscenaRequest;
import com.guardia.core.dto.response.EscenaChecklistResponse;
import com.guardia.core.dto.response.EscenaResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.EscenaChecklist;
import com.guardia.core.model.Evidencia;
import com.guardia.core.model.EscenaNegativa;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoEscena;
import com.guardia.core.model.enums.PasoChecklist;
import com.guardia.core.repository.EscenaChecklistRepository;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link EscenaServiceImpl}.
 * Cubre la máquina de estados del checklist (crear, avanzar, iniciar, cerrar, liberar) y sus
 * reglas de negocio asociadas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EscenaServiceImpl - Pruebas Unitarias")
class EscenaServiceImplTest {

    @Mock private EscenaRepository escenaRepository;
    @Mock private ExpedienteRepository expedienteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EscenaChecklistRepository escenaChecklistRepository;
    @Mock private HashStrategy hashStrategy;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EscenaServiceImpl escenaService;

    private Expediente expedienteEjemplo;
    private Usuario investigadorEjemplo;
    private Escena escenaEjemplo;
    private UUID investigadorId;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        investigadorId = UUID.randomUUID();
        expedienteEjemplo = Expediente.builder().id(1L).folio("EXP-2026-AAAA1111").build();
        investigadorEjemplo = Usuario.builder()
                .id(investigadorId).username("cruiz").password("hash").fullName("Carlos Ruiz").rol("ANALISTA").build();
        escenaEjemplo = Escena.builder()
                .id(1L)
                .expediente(expedienteEjemplo)
                .levantadaPor(investigadorEjemplo)
                .estadoChecklist("PENDIENTE")
                .pasoActual(PasoChecklist.ASEGURAMIENTO_PERIMETRO)
                .estado(EstadoEscena.ACTIVA)
                .evidencias(new ArrayList<>())
                .escenasNegativas(new ArrayList<>())
                .build();
    }

    /** Construye un paso de checklist con los valores indicados. */
    private EscenaChecklist paso(Long id, PasoChecklist tipo, int orden, boolean completado) {
        return EscenaChecklist.builder()
                .id(id).paso(tipo).orden(orden).completado(completado)
                .escena(escenaEjemplo)
                .build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear la escena con checklist inicial de 4 pasos y estado PENDIENTE")
        void debeCrearEscenaConChecklistInicial() {
            // Arrange
            EscenaRequest request = new EscenaRequest(1L, investigadorId);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(investigadorId)).thenReturn(Optional.of(investigadorEjemplo));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.crear(request);

            // Assert
            assertThat(resultado.estadoChecklist()).isEqualTo("PENDIENTE");
            assertThat(resultado.pasoActual()).isEqualTo(PasoChecklist.ASEGURAMIENTO_PERIMETRO.name());
            assertThat(resultado.expedienteId()).isEqualTo(1L);
            assertThat(resultado.levantadaPor().fullName()).isEqualTo("Carlos Ruiz");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el expediente no existe")
        void debeLanzarExcepcionCuandoExpedienteNoExiste() {
            // Arrange
            EscenaRequest request = new EscenaRequest(99L, investigadorId);
            when(expedienteRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verifyNoInteractions(escenaRepository);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            UUID inexistente = UUID.randomUUID();
            EscenaRequest request = new EscenaRequest(1L, inexistente);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(escenaRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> escenaService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todas las escenas registradas")
    void debeRetornarTodasLasEscenas() {
        // Arrange
        when(escenaRepository.findAll()).thenReturn(List.of(escenaEjemplo));

        // Act
        List<EscenaResponse> resultado = escenaService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorExpediente() debe delegar en el repositorio filtrando por expedienteId")
    void debeRetornarEscenasPorExpediente() {
        // Arrange
        when(escenaRepository.findByExpedienteId(1L)).thenReturn(List.of(escenaEjemplo));

        // Act
        List<EscenaResponse> resultado = escenaService.obtenerPorExpediente(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(escenaRepository).findByExpedienteId(1L);
    }

    @Test
    @DisplayName("obtenerPorInvestigador() debe delegar en el repositorio filtrando por usuarioId")
    void debeRetornarEscenasPorInvestigador() {
        // Arrange
        when(escenaRepository.findByLevantadaPorId(investigadorId)).thenReturn(List.of(escenaEjemplo));

        // Act
        List<EscenaResponse> resultado = escenaService.obtenerPorInvestigador(investigadorId);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar la escena cuando existe")
        void debeEliminarEscenaExistente() {
            // Arrange
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act
            escenaService.eliminar(1L);

            // Assert
            verify(escenaRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminar() {
            // Arrange
            when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.eliminar(99L)).isInstanceOf(ResourceNotFoundException.class);
            verify(escenaRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("avanzarPaso()")
    class AvanzarPaso {

        @Test
        @DisplayName("Debe avanzar del primer al segundo paso registrando timestamps")
        void debeAvanzarAlSegundoPaso() {
            // Arrange
            EscenaChecklist paso1 = paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, false);
            EscenaChecklist paso2 = paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L))
                    .thenReturn(List.of(paso1, paso2));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.avanzarPaso(1L);

            // Assert
            assertThat(paso1.getCompletado()).isTrue();
            assertThat(paso1.getFechaCierre()).isNotNull();
            assertThat(paso2.getFechaInicio()).isNotNull();
            assertThat(resultado.pasoActual()).isEqualTo(PasoChecklist.DOCUMENTACION_EVIDENCIA.name());
            verify(escenaChecklistRepository).save(paso1);
            verify(escenaChecklistRepository).save(paso2);
        }

        @Test
        @DisplayName("Debe completar el checklist cuando se avanza el último paso")
        void debeCompletarChecklistEnUltimoPaso() {
            // Arrange
            EscenaChecklist ultimoPaso = paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L))
                    .thenReturn(List.of(ultimoPaso));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.avanzarPaso(1L);

            // Assert
            assertThat(resultado.pasoActual()).isNull();
            assertThat(resultado.estadoChecklist()).isEqualTo("COMPLETADO");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el checklist ya está completado")
        void debeLanzarExcepcionCuandoYaCompletado() {
            // Arrange
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.avanzarPaso(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Checklist ya completado.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException al avanzar DOCUMENTACION_EVIDENCIA sin evidencias registradas")
        void debeLanzarExcepcionSinEvidencias() {
            // Arrange
            EscenaChecklist pasoDoc = paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of(pasoDoc));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.avanzarPaso(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Debe registrar al menos una evidencia antes de continuar.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException al avanzar DOCUMENTACION_EVIDENCIA sin escenas negativas")
        void debeLanzarExcepcionSinEscenasNegativas() {
            // Arrange
            escenaEjemplo.getEvidencias().add(Evidencia.builder().id(1L).build());
            EscenaChecklist pasoDoc = paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of(pasoDoc));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.avanzarPaso(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("escena negativa");
        }

        @Test
        @DisplayName("Debe permitir avanzar DOCUMENTACION_EVIDENCIA cuando hay evidencias y escenas negativas")
        void debeAvanzarDocumentacionConEvidenciasYNegativas() {
            // Arrange
            escenaEjemplo.getEvidencias().add(Evidencia.builder().id(1L).build());
            escenaEjemplo.getEscenasNegativas().add(EscenaNegativa.builder().id(1L).build());
            EscenaChecklist pasoDoc = paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false);
            EscenaChecklist pasoSiguiente = paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L))
                    .thenReturn(List.of(pasoDoc, pasoSiguiente));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.avanzarPaso(1L);

            // Assert
            assertThat(resultado.pasoActual()).isEqualTo(PasoChecklist.RECOLECCION_EMBALAJE.name());
        }
    }

    @Nested
    @DisplayName("iniciarChecklist()")
    class IniciarChecklist {

        @Test
        @DisplayName("Debe iniciar el checklist y registrar la fecha de inicio del primer paso")
        void debeIniciarChecklistExitosamente() {
            // Arrange
            EscenaChecklist primerPaso = paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, false);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of(primerPaso));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.iniciarChecklist(1L);

            // Assert
            assertThat(resultado.estadoChecklist()).isEqualTo("INICIADO");
            assertThat(primerPaso.getFechaInicio()).isNotNull();
            verify(escenaChecklistRepository).save(primerPaso);
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la escena ya fue iniciada")
        void debeLanzarExcepcionCuandoYaIniciada() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("INICIADO");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.iniciarChecklist(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("La escena ya fue iniciada o completada.");
        }
    }

    @Nested
    @DisplayName("cerrar()")
    class Cerrar {

        @Test
        @DisplayName("Debe cerrar la escena cuando está iniciada")
        void debeCerrarEscenaIniciada() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("INICIADO");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.cerrar(1L);

            // Assert
            assertThat(resultado.estadoChecklist()).isEqualTo("CERRADO");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la escena ya está cerrada")
        void debeLanzarExcepcionCuandoYaCerrada() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("CERRADO");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.cerrar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("La escena ya está cerrada.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la escena no ha sido iniciada")
        void debeLanzarExcepcionCuandoNoIniciada() {
            // Arrange: estadoChecklist == "PENDIENTE" (valor por defecto del fixture)
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.cerrar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("La escena debe estar iniciada antes de cerrarla.");
        }
    }

    @Test
    @DisplayName("bloquearEdicion() debe cambiar el estado del checklist a BLOQUEADO")
    void debeBloquearEdicion() {
        // Arrange
        when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
        when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EscenaResponse resultado = escenaService.bloquearEdicion(1L);

        // Assert
        assertThat(resultado.estadoChecklist()).isEqualTo("BLOQUEADO");
    }

    @Test
    @DisplayName("validarSecuencia() debe delegar en la entidad")
    void debeValidarSecuencia() {
        // Arrange
        escenaEjemplo.setChecklist(List.of(paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true)));
        when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

        // Act
        boolean resultado = escenaService.validarSecuencia(1L);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("obtenerChecklist() debe mapear cada paso a su respuesta correspondiente")
    void debeObtenerChecklist() {
        // Arrange
        EscenaChecklist p1 = paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true);
        when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
        when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of(p1));

        // Act
        List<EscenaChecklistResponse> resultado = escenaService.obtenerChecklist(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).paso()).isEqualTo(PasoChecklist.ASEGURAMIENTO_PERIMETRO);
        assertThat(resultado.get(0).completado()).isTrue();
    }

    @Nested
    @DisplayName("liberar()")
    class Liberar {

        @Test
        @DisplayName("Debe liberar la escena cuando todos los pasos previos están completos y firmados")
        void debeLiberarEscenaExitosamente() {
            // Arrange
            EscenaChecklist p1 = paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true);
            p1.setFechaCierre(java.time.LocalDateTime.now());
            EscenaChecklist p2 = paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, true);
            p2.setFechaCierre(java.time.LocalDateTime.now());
            EscenaChecklist p3 = paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, true);
            p3.setFechaCierre(java.time.LocalDateTime.now());
            EscenaChecklist pLiberacion = paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false);

            LiberarEscenaRequest request = new LiberarEscenaRequest(investigadorId, "Todo en orden");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L))
                    .thenReturn(List.of(p1, p2, p3, pLiberacion));
            when(usuarioRepository.findById(investigadorId)).thenReturn(Optional.of(investigadorEjemplo));
            when(hashStrategy.calcular(anyString())).thenReturn("hash-liberacion");
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.liberar(1L, request);

            // Assert
            assertThat(resultado.hashLiberacion()).isEqualTo("hash-liberacion");
            assertThat(resultado.observacionesLiberacion()).isEqualTo("Todo en orden");
            assertThat(resultado.liberadaPor().fullName()).isEqualTo("Carlos Ruiz");
            assertThat(pLiberacion.getCompletado()).isTrue();
            verify(eventPublisher).publishEvent(any(com.guardia.core.EscenaLiberadaEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la escena ya fue liberada")
        void debeLanzarExcepcionCuandoYaLiberada() {
            // Arrange
            escenaEjemplo.setEstado(EstadoEscena.LIBERADA);
            LiberarEscenaRequest request = new LiberarEscenaRequest(investigadorId, null);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("La escena ya fue liberada formalmente y su registro está sellado.");
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el checklist no tiene configurado el paso de liberación")
        void debeLanzarExcepcionSinPasoDeLiberacion() {
            // Arrange
            LiberarEscenaRequest request = new LiberarEscenaRequest(investigadorId, null);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("paso de liberación");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando algún paso previo no está completo o firmado")
        void debeLanzarExcepcionConPasosPreviosIncompletos() {
            // Arrange
            EscenaChecklist p1 = paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, false);
            EscenaChecklist pLiberacion = paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false);
            LiberarEscenaRequest request = new LiberarEscenaRequest(investigadorId, null);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of(p1, pLiberacion));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pasos previos del checklist");
            verifyNoInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador responsable no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            EscenaChecklist pLiberacion = paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false);
            UUID inexistente = UUID.randomUUID();
            LiberarEscenaRequest request = new LiberarEscenaRequest(inexistente, null);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(List.of(pLiberacion));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(escenaRepository, never()).save(any());
        }
    }
}
