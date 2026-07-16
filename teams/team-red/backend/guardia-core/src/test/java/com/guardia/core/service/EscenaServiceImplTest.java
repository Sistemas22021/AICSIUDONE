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

    @Mock
    private EscenaRepository escenaRepository;

    @Mock
    private ExpedienteRepository expedienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EscenaChecklistRepository escenaChecklistRepository;

    @Mock
    private HashStrategy hashStrategy;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EscenaServiceImpl escenaService;

    private Expediente expedienteEjemplo;
    private Usuario investigadorEjemplo;
    private Escena escenaEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        expedienteEjemplo = Expediente.builder().id(1L).folio("EXP-2026-AAAA1111").build();
        investigadorEjemplo = Usuario.builder().id(1L).nombre("Carlos Ruiz").identificacion("V-1").correo("c@x.com").build();
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
            EscenaRequest request = new EscenaRequest(1L, 1L);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(investigadorEjemplo));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.crear(request);

            // Assert
            assertThat(resultado.estadoChecklist()).isEqualTo("PENDIENTE");
            assertThat(resultado.pasoActual()).isEqualTo(PasoChecklist.ASEGURAMIENTO_PERIMETRO.name());
            assertThat(resultado.expedienteId()).isEqualTo(1L);
            assertThat(resultado.levantadaPor().nombre()).isEqualTo("Carlos Ruiz");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el expediente no existe")
        void debeLanzarExcepcionCuandoExpedienteNoExiste() {
            // Arrange
            EscenaRequest request = new EscenaRequest(99L, 1L);
            when(expedienteRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(escenaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            EscenaRequest request = new EscenaRequest(1L, 77L);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(escenaRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar la escena cuando existe")
    void debeRetornarEscenaCuandoExiste() {
        // Arrange
        when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

        // Act
        EscenaResponse resultado = escenaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado.id()).isEqualTo(1L);
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
    @DisplayName("obtenerTodos() debe retornar todas las escenas mapeadas")
    void debeRetornarTodasLasEscenas() {
        // Arrange
        when(escenaRepository.findAll()).thenReturn(List.of(escenaEjemplo));

        // Act
        List<EscenaResponse> resultado = escenaService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorExpediente() debe filtrar por expedienteId")
    void debeRetornarEscenasPorExpediente() {
        // Arrange
        when(escenaRepository.findByExpedienteId(1L)).thenReturn(List.of(escenaEjemplo));

        // Act
        List<EscenaResponse> resultado = escenaService.obtenerPorExpediente(1L);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorInvestigador() debe filtrar por el investigador que levantó la escena")
    void debeRetornarEscenasPorInvestigador() {
        // Arrange
        when(escenaRepository.findByLevantadaPorId(1L)).thenReturn(List.of(escenaEjemplo));

        // Act
        List<EscenaResponse> resultado = escenaService.obtenerPorInvestigador(1L);

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
        void debeLanzarExcepcionAlEliminarInexistente() {
            // Arrange
            when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(escenaRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("avanzarPaso()")
    class AvanzarPaso {

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el checklist ya fue completado")
        void debeLanzarExcepcionCuandoChecklistCompletado() {
            // Arrange: todos los pasos ya completados => obtenerPasoActual() retorna null
            List<EscenaChecklist> checklist = List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, true),
                    paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, true),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, true)
            );
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);

            // Act & Assert
            assertThatThrownBy(() -> escenaService.avanzarPaso(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Checklist ya completado.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException si se intenta avanzar DOCUMENTACION_EVIDENCIA sin evidencias")
        void debeLanzarExcepcionSinEvidencias() {
            // Arrange
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false),
                    paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, false),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            escenaEjemplo.setEvidencias(new ArrayList<>());
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);

            // Act & Assert
            assertThatThrownBy(() -> escenaService.avanzarPaso(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Debe registrar al menos una evidencia antes de continuar.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException si hay evidencias pero no escena negativa registrada")
        void debeLanzarExcepcionSinEscenaNegativa() {
            // Arrange
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false),
                    paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, false),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            escenaEjemplo.setEvidencias(new ArrayList<>(List.of(Evidencia.builder().id(1L).build())));
            escenaEjemplo.setEscenasNegativas(new ArrayList<>());
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);

            // Act & Assert
            assertThatThrownBy(() -> escenaService.avanzarPaso(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("escena negativa");
        }

        @Test
        @DisplayName("Debe completar el paso actual y avanzar al siguiente, iniciando su timestamp")
        void debeAvanzarAlSiguientePaso() {
            // Arrange: paso 1 pendiente, resto pendientes; la lista es mutable y compartida por referencia
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, false),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false),
                    paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, false),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);
            when(escenaChecklistRepository.save(any(EscenaChecklist.class))).thenAnswer(inv -> inv.getArgument(0));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.avanzarPaso(1L);

            // Assert
            assertThat(checklist.get(0).getCompletado()).isTrue();
            assertThat(checklist.get(0).getFechaCierre()).isNotNull();
            assertThat(checklist.get(1).getFechaInicio()).isNotNull();
            assertThat(resultado.pasoActual()).isEqualTo(PasoChecklist.DOCUMENTACION_EVIDENCIA.name());
        }

        @Test
        @DisplayName("Debe completar el checklist al avanzar el último paso (LIBERACION_ESCENA)")
        void debeCompletarChecklistEnUltimoPaso() {
            // Arrange
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, true),
                    paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, true),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);
            when(escenaChecklistRepository.save(any(EscenaChecklist.class))).thenAnswer(inv -> inv.getArgument(0));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.avanzarPaso(1L);

            // Assert
            assertThat(resultado.pasoActual()).isNull();
            assertThat(resultado.estadoChecklist()).isEqualTo("COMPLETADO");
            assertThat(resultado.cierreProceso()).isNotNull();
        }
    }

    @Nested
    @DisplayName("iniciarChecklist()")
    class IniciarChecklist {

        @Test
        @DisplayName("Debe iniciar el checklist cuando está PENDIENTE")
        void debeIniciarChecklistExitosamente() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("PENDIENTE");
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, false)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);
            when(escenaChecklistRepository.save(any(EscenaChecklist.class))).thenAnswer(inv -> inv.getArgument(0));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.iniciarChecklist(1L);

            // Assert
            assertThat(resultado.estadoChecklist()).isEqualTo("INICIADO");
            assertThat(resultado.inicioProceso()).isNotNull();
            assertThat(checklist.get(0).getFechaInicio()).isNotNull();
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando ya fue iniciado")
        void debeLanzarExcepcionCuandoYaIniciado() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("INICIADO");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.iniciarChecklist(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("La escena ya fue iniciada o completada.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando ya fue completado")
        void debeLanzarExcepcionCuandoYaCompletado() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("COMPLETADO");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.iniciarChecklist(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("cerrar()")
    class Cerrar {

        @Test
        @DisplayName("Debe cerrar la escena cuando está INICIADO")
        void debeCerrarEscenaIniciada() {
            // Arrange
            escenaEjemplo.setEstadoChecklist("INICIADO");
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.cerrar(1L);

            // Assert
            assertThat(resultado.estadoChecklist()).isEqualTo("CERRADO");
            assertThat(resultado.cierreProceso()).isNotNull();
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando ya está cerrada")
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
            // Arrange
            escenaEjemplo.setEstadoChecklist("PENDIENTE");
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

    @Nested
    @DisplayName("liberar()")
    class Liberar {

        @Test
        @DisplayName("Debe liberar la escena cuando todos los pasos previos están completos y firmados")
        void debeLiberarEscenaExitosamente() {
            // Arrange
            LiberarEscenaRequest request = new LiberarEscenaRequest(1L, "Todo en orden");
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    completo(paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true)),
                    completo(paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, true)),
                    completo(paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, true)),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(investigadorEjemplo));
            when(hashStrategy.calcular(anyString())).thenReturn("hash-liberacion");
            when(escenaChecklistRepository.save(any(EscenaChecklist.class))).thenAnswer(inv -> inv.getArgument(0));
            when(escenaRepository.save(any(Escena.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            EscenaResponse resultado = escenaService.liberar(1L, request);

            // Assert
            assertThat(resultado.hashLiberacion()).isEqualTo("hash-liberacion");
            assertThat(resultado.observacionesLiberacion()).isEqualTo("Todo en orden");
            assertThat(resultado.estado()).isEqualTo(EstadoEscena.LIBERADA.name());
            verify(eventPublisher).publishEvent(any());
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la escena ya fue liberada")
        void debeLanzarExcepcionCuandoYaLiberada() {
            // Arrange
            escenaEjemplo.setEstado(EstadoEscena.LIBERADA);
            LiberarEscenaRequest request = new LiberarEscenaRequest(1L, null);
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("La escena ya fue liberada formalmente y su registro está sellado.");
            verify(escenaChecklistRepository, never()).findByEscenaIdOrderByOrden(any());
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el checklist no tiene paso de liberación configurado")
        void debeLanzarExcepcionCuandoNoHayPasoLiberacion() {
            // Arrange
            LiberarEscenaRequest request = new LiberarEscenaRequest(1L, null);
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("paso de liberación");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando los pasos previos no están completos")
        void debeLanzarExcepcionCuandoPasosPreviosIncompletos() {
            // Arrange
            LiberarEscenaRequest request = new LiberarEscenaRequest(1L, null);
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    completo(paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true)),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false), // incompleto
                    paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, false),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pasos previos del checklist");
            verify(usuarioRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador responsable no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            LiberarEscenaRequest request = new LiberarEscenaRequest(77L, null);
            List<EscenaChecklist> checklist = new ArrayList<>(List.of(
                    completo(paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true)),
                    completo(paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, true)),
                    completo(paso(3L, PasoChecklist.RECOLECCION_EMBALAJE, 3, true)),
                    paso(4L, PasoChecklist.LIBERACION_ESCENA, 4, false)
            ));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
            when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> escenaService.liberar(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        /** Marca un paso de checklist como cerrado (fechaCierre no nula) para simular un paso firmado. */
        private EscenaChecklist completo(EscenaChecklist checklist) {
            checklist.setFechaCierre(java.time.LocalDateTime.now());
            return checklist;
        }
    }

    @Nested
    @DisplayName("validarSecuencia()")
    class ValidarSecuencia {

        @Test
        @DisplayName("Debe retornar true cuando la secuencia de pasos completados es consistente")
        void debeRetornarTrueParaSecuenciaValida() {
            // Arrange
            escenaEjemplo.setChecklist(new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false)
            )));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act
            boolean resultado = escenaService.validarSecuencia(1L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando hay un paso completado con un paso anterior incompleto")
        void debeRetornarFalseParaSecuenciaInvalida() {
            // Arrange
            escenaEjemplo.setChecklist(new ArrayList<>(List.of(
                    paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, false),
                    paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, true)
            )));
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act
            boolean resultado = escenaService.validarSecuencia(1L);

            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando el checklist está vacío")
        void debeRetornarFalseParaChecklistVacio() {
            // Arrange
            escenaEjemplo.setChecklist(new ArrayList<>());
            when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));

            // Act
            boolean resultado = escenaService.validarSecuencia(1L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }

    @Test
    @DisplayName("obtenerChecklist() debe retornar los pasos ordenados mapeados a response")
    void debeRetornarChecklistOrdenado() {
        // Arrange
        List<EscenaChecklist> checklist = List.of(
                paso(1L, PasoChecklist.ASEGURAMIENTO_PERIMETRO, 1, true),
                paso(2L, PasoChecklist.DOCUMENTACION_EVIDENCIA, 2, false)
        );
        when(escenaRepository.findById(1L)).thenReturn(Optional.of(escenaEjemplo));
        when(escenaChecklistRepository.findByEscenaIdOrderByOrden(1L)).thenReturn(checklist);

        // Act
        List<EscenaChecklistResponse> resultado = escenaService.obtenerChecklist(1L);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).paso()).isEqualTo(PasoChecklist.ASEGURAMIENTO_PERIMETRO);
    }

    @Test
    @DisplayName("obtenerChecklist() debe lanzar ResourceNotFoundException cuando la escena no existe")
    void debeLanzarExcepcionAlObtenerChecklistDeEscenaInexistente() {
        // Arrange
        when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> escenaService.obtenerChecklist(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(escenaChecklistRepository, never()).findByEscenaIdOrderByOrden(any());
    }
}
