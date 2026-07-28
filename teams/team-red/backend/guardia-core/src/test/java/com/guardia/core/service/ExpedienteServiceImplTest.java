// Ruta destino: src/test/java/com/guardia/core/service/ExpedienteServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.SelloStrategy;
import com.guardia.core.dto.request.CoordenadasRequest;
import com.guardia.core.dto.request.DelitoRequest;
import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.request.InvolucradosRequest;
import com.guardia.core.dto.request.SubDelitoRequest;
import com.guardia.core.dto.request.UbicacionRequest;
import com.guardia.core.dto.response.ExpedienteActivoResponse;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.dto.response.VerificacionHashResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Localizacion;
import com.guardia.core.model.SubtipoDelito;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoExpediente;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.InvolucradoRepository;
import com.guardia.core.repository.LocalizacionRepository;
import com.guardia.core.repository.SubtipoDelitoRepository;
import com.guardia.core.repository.TipoDelitoRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link ExpedienteServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExpedienteServiceImpl - Pruebas Unitarias")
class ExpedienteServiceImplTest {

    @Mock private ExpedienteRepository expedienteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private TipoDelitoRepository tipoDelitoRepository;
    @Mock private SubtipoDelitoRepository subtipoDelitoRepository;
    @Mock private LocalizacionRepository localizacionRepository;
    @Mock private EscenaRepository escenaRepository;
    @Mock private SelloStrategy selloStrategy;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private InvolucradoRepository involucradoRepository;

    @InjectMocks
    private ExpedienteServiceImpl expedienteService;

    private Expediente expedienteEjemplo;
    private UUID agenteId;
    private Usuario agenteEjemplo;

    @BeforeEach
    void setUp() {
        agenteId = UUID.randomUUID();
        agenteEjemplo = Usuario.builder()
                .id(agenteId).username("agomez").password("hash").fullName("Agente Gómez").rol("OFICIAL").build();
        expedienteEjemplo = Expediente.builder()
                .id(1L).folio("EXP-2026-AAAA1111").descripcionHecho("Robo").estadoExpediente(EstadoExpediente.BORRADOR)
                .build();
    }

    private UbicacionRequest ubicacionRequest() {
        UbicacionRequest u = new UbicacionRequest();
        u.setMunicipio("Libertador");
        u.setSector("Catia");
        u.setDireccion("Av. Principal");
        u.setReferencia("Cerca de la plaza");
        CoordenadasRequest c = new CoordenadasRequest();
        c.setLatitud(10.5);
        c.setLongitud(-66.9);
        u.setCoordenadas(c);
        return u;
    }

    private DelitoRequest delitoRequest(String delito, String subtipo) {
        DelitoRequest d = new DelitoRequest();
        d.setDelito(delito);
        SubDelitoRequest sub = new SubDelitoRequest();
        sub.setNombre(subtipo);
        d.setSubDelito(sub);
        d.setFechaHecho(LocalDate.of(2026, 1, 15));
        d.setHoraInicioHecho(LocalTime.of(20, 0));
        d.setHechoEnCurso(false);
        return d;
    }

    private InvolucradosRequest involucrado(String nombre, String cedula) {
        InvolucradosRequest v = new InvolucradosRequest();
        v.setNombre(nombre);
        v.setCedula(cedula);
        v.setTelefono("0414-1234567");
        v.setNacionalidad("Venezolana");
        v.setDireccion("Calle Falsa 123");
        return v;
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear el expediente completo con ubicación, delitos, víctimas y denunciante")
        void debeCrearExpedienteCompleto() {
            // Arrange
            ExpedienteRequest request = new ExpedienteRequest();
            request.setUbicacion(ubicacionRequest());
            request.setDelitos(List.of(delitoRequest("HOMICIDIO", "HOMICIDIO_CULPOSO")));
            request.setDescripcion("Descripción del hecho");
            request.setEsDenunciaFormal(true);
            request.setVictimas(List.of(involucrado("Maria Lopez", "V-9999999")));
            request.setDenunciante(involucrado("Pedro Diaz", "V-8888888"));

            TipoDelito tipoDelito = TipoDelito.builder().id(1L).nombre("HOMICIDIO").requiereSubtipo(true).build();
            SubtipoDelito subtipoDelito = SubtipoDelito.builder()
                    .id(10L).nombre("HOMICIDIO_CULPOSO").tipoDelito(tipoDelito).build();

            when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tipoDelitoRepository.findByNombre("HOMICIDIO")).thenReturn(Optional.of(tipoDelito));
            when(subtipoDelitoRepository.findByTipoDelitoId(1L)).thenReturn(List.of(subtipoDelito));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert
            assertThat(resultado.folio()).startsWith("EXP-2026-");
            assertThat(resultado.descripcionHecho()).isEqualTo("Descripción del hecho");
            assertThat(resultado.tipoDelito().nombre()).isEqualTo("HOMICIDIO");
            assertThat(resultado.subtipoDelito().nombre()).isEqualTo("HOMICIDIO_CULPOSO");
            assertThat(resultado.involucrados()).hasSize(2); // víctima + denunciante
            assertThat(resultado.localizacion().municipio()).isEqualTo("Libertador");
            verify(eventPublisher).publishEvent(any(com.guardia.core.ExpedienteRegistradoEvent.class));
        }

        @Test
        @DisplayName("Debe crear el expediente mínimo sin ubicación ni denunciante")
        void debeCrearExpedienteMinimo() {
            // Arrange
            ExpedienteRequest request = new ExpedienteRequest();
            request.setDescripcion("Descripción mínima");
            request.setDelitos(List.of());
            request.setVictimas(List.of(involucrado("Ana Perez", "V-1111111")));

            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert
            assertThat(resultado.localizacion()).isNull();
            assertThat(resultado.tipoDelito()).isNull();
            assertThat(resultado.involucrados()).hasSize(1);
            verifyNoInteractions(localizacionRepository, tipoDelitoRepository);
        }

        @Test
        @DisplayName("No debe promover el subtipo cuando el tipo de delito no coincide con ningún nombre existente")
        void noDebePromoverSubtipoSinTipoDelitoEncontrado() {
            // Arrange
            ExpedienteRequest request = new ExpedienteRequest();
            request.setDescripcion("Descripción");
            request.setDelitos(List.of(delitoRequest("DELITO_DESCONOCIDO", "SUBTIPO_X")));
            request.setVictimas(List.of(involucrado("Ana Perez", "V-1111111")));

            when(tipoDelitoRepository.findByNombre("DELITO_DESCONOCIDO")).thenReturn(Optional.empty());
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert
            assertThat(resultado.tipoDelito()).isNull();
            assertThat(resultado.subtipoDelito()).isNull();
            verifyNoInteractions(subtipoDelitoRepository);
        }
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
    void debeLanzarExcepcionCuandoNoExistePorId() {
        when(expedienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> expedienteService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Nested
    @DisplayName("obtenerPorFolio()")
    class ObtenerPorFolio {

        @Test
        @DisplayName("Debe retornar el expediente cuando el folio existe")
        void debeRetornarExpedienteCuandoFolioExiste() {
            when(expedienteRepository.findByFolio("EXP-2026-AAAA1111")).thenReturn(Optional.of(expedienteEjemplo));

            ExpedienteResponse resultado = expedienteService.obtenerPorFolio("EXP-2026-AAAA1111");

            assertThat(resultado.folio()).isEqualTo("EXP-2026-AAAA1111");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException con mensaje específico cuando el folio no existe")
        void debeLanzarExcepcionCuandoFolioNoExiste() {
            when(expedienteRepository.findByFolio("INEXISTENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> expedienteService.obtenerPorFolio("INEXISTENTE"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Expediente con folio INEXISTENTE no encontrado.");
        }
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los expedientes")
    void debeRetornarTodosLosExpedientes() {
        when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo));
        assertThat(expedienteService.obtenerTodos()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorEstado() debe delegar en el repositorio")
    void debeRetornarPorEstado() {
        when(expedienteRepository.findByEstadoExpediente(EstadoExpediente.BORRADOR))
                .thenReturn(List.of(expedienteEjemplo));
        assertThat(expedienteService.obtenerPorEstado(EstadoExpediente.BORRADOR)).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorCreador() debe delegar en el repositorio filtrando por usuarioId")
    void debeRetornarPorCreador() {
        when(expedienteRepository.findByCreadoPorId(agenteId)).thenReturn(List.of(expedienteEjemplo));
        assertThat(expedienteService.obtenerPorCreador(agenteId)).hasSize(1);
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar descripción y fecha del hecho de un expediente en BORRADOR")
        void debeActualizarExpedienteExistente() {
            ExpedienteRequest request = new ExpedienteRequest();
            request.setDescripcion("Nueva descripción");
            request.setDelitos(List.of(delitoRequest("ROBO", "ROBO_AGRAVADO")));

            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            ExpedienteResponse resultado = expedienteService.actualizar(1L, request);

            assertThat(resultado.descripcionHecho()).isEqualTo("Nueva descripción");
            assertThat(resultado.fechaHecho()).isEqualTo(java.time.LocalDateTime.of(2026, 1, 15, 20, 0));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException al intentar modificar un expediente sellado")
        void debeLanzarExcepcionAlActualizarSellado() {
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.PROCESADO_Y_SELLADO);
            ExpedienteRequest request = new ExpedienteRequest();
            request.setDescripcion("Cambio no permitido");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            assertThatThrownBy(() -> expedienteService.actualizar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("No se puede modificar un expediente sellado o archivado.");
            verify(expedienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el expediente cuando está en BORRADOR")
        void debeEliminarExpedienteEnBorrador() {
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            expedienteService.eliminar(1L);
            verify(expedienteRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar BusinessException al eliminar un expediente que no está en BORRADOR")
        void debeLanzarExcepcionAlEliminarNoBorrador() {
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.INVESTIGACION_ACTIVA);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            assertThatThrownBy(() -> expedienteService.eliminar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Solo se pueden eliminar expedientes en estado BORRADOR.");
            verify(expedienteRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("sellar()")
    class Sellar {

        @Test
        @DisplayName("Debe sellar el expediente cuando tiene todos los datos requeridos")
        void debeSellarExpedienteExitosamente() {
            expedienteEjemplo.setTipoDelito(TipoDelito.builder().id(1L).nombre("ROBO").build());
            expedienteEjemplo.setMunicipio("Libertador");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(agenteId)).thenReturn(Optional.of(agenteEjemplo));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            ExpedienteResponse resultado = expedienteService.sellar(1L, agenteId);

            assertThat(resultado).isNotNull();
            verify(selloStrategy).aplicar(eq(expedienteEjemplo), eq(agenteEjemplo), any());
            verify(eventPublisher).publishEvent(any(com.guardia.core.SelloExpedienteEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el expediente ya está sellado")
        void debeLanzarExcepcionCuandoYaSellado() {
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.PROCESADO_Y_SELLADO);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            assertThatThrownBy(() -> expedienteService.sellar(1L, agenteId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("El expediente ya está sellado.");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el expediente no tiene los datos requeridos")
        void debeLanzarExcepcionCuandoDatosIncompletos() {
            // expedienteEjemplo no tiene tipoDelito ni municipio/localización -> validarDatos() == false
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            assertThatThrownBy(() -> expedienteService.sellar(1L, agenteId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("El expediente no tiene todos los datos requeridos para sellarse.");
            verifyNoInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el agente sellador no existe")
        void debeLanzarExcepcionCuandoAgenteNoExiste() {
            expedienteEjemplo.setTipoDelito(TipoDelito.builder().id(1L).nombre("ROBO").build());
            expedienteEjemplo.setMunicipio("Libertador");
            UUID inexistente = UUID.randomUUID();
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> expedienteService.sellar(1L, inexistente))
                    .isInstanceOf(ResourceNotFoundException.class);
            verifyNoInteractions(selloStrategy);
        }
    }

    @Test
    @DisplayName("cambiarEstado() debe actualizar el estado del expediente")
    void debeCambiarEstado() {
        when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
        when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

        ExpedienteResponse resultado = expedienteService.cambiarEstado(1L, EstadoExpediente.EN_REVISION);

        assertThat(resultado.estadoExpediente()).isEqualTo(EstadoExpediente.EN_REVISION);
    }

    @Nested
    @DisplayName("asignarInvestigador()")
    class AsignarInvestigador {

        @Test
        @DisplayName("Debe asignar el investigador y cambiar el estado a ASIGNADO_A_EQUIPO")
        void debeAsignarInvestigadorExitosamente() {
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(agenteId)).thenReturn(Optional.of(agenteEjemplo));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            ExpedienteResponse resultado = expedienteService.asignarInvestigador(1L, agenteId);

            assertThat(resultado.estadoExpediente()).isEqualTo(EstadoExpediente.ASIGNADO_A_EQUIPO);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            UUID inexistente = UUID.randomUUID();
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> expedienteService.asignarInvestigador(1L, inexistente))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(expedienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("vincularEscena()")
    class VincularEscena {

        @Test
        @DisplayName("Debe vincular la escena al expediente")
        void debeVincularEscenaExitosamente() {
            Escena escena = Escena.builder().id(5L).build();
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(escenaRepository.findById(5L)).thenReturn(Optional.of(escena));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            ExpedienteResponse resultado = expedienteService.vincularEscena(1L, 5L);

            assertThat(resultado.escenas()).hasSize(1);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la escena no existe")
        void debeLanzarExcepcionCuandoEscenaNoExiste() {
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> expedienteService.vincularEscena(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("asignarFechaHecho() debe parsear y asignar la fecha del hecho")
    void debeAsignarFechaHecho() {
        when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
        when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

        ExpedienteResponse resultado = expedienteService.asignarFechaHecho(1L, "2026-01-15T20:00:00");

        assertThat(resultado.fechaHecho()).isEqualTo(java.time.LocalDateTime.of(2026, 1, 15, 20, 0));
    }

    @Test
    @DisplayName("validarDatos() debe delegar en la entidad")
    void debeValidarDatos() {
        when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
        assertThat(expedienteService.validarDatos(1L)).isFalse();
    }

    @Nested
    @DisplayName("verificarIntegridad()")
    class VerificarIntegridad {

        @Test
        @DisplayName("Debe indicar que el expediente no ha sido sellado cuando no tiene hash")
        void debeIndicarNoSellado() {
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            VerificacionHashResponse resultado = expedienteService.verificarIntegridad(1L);

            assertThat(resultado.integro()).isFalse();
            assertThat(resultado.mensaje()).isEqualTo("El expediente no ha sido sellado.");
        }

        @Test
        @DisplayName("Debe reportar integridad verificada cuando el hash recalculado coincide")
        void debeReportarIntegridadVerificada() {
            expedienteEjemplo.setHashIntegridad("hash-original");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(selloStrategy.recalcularHash(expedienteEjemplo)).thenReturn("hash-original");

            VerificacionHashResponse resultado = expedienteService.verificarIntegridad(1L);

            assertThat(resultado.integro()).isTrue();
        }

        @Test
        @DisplayName("Debe reportar alerta de alteración cuando el hash recalculado no coincide")
        void debeReportarAlteracion() {
            expedienteEjemplo.setHashIntegridad("hash-original");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(selloStrategy.recalcularHash(expedienteEjemplo)).thenReturn("hash-modificado");

            VerificacionHashResponse resultado = expedienteService.verificarIntegridad(1L);

            assertThat(resultado.integro()).isFalse();
            assertThat(resultado.hashAlmacenado()).isEqualTo("hash-original");
            assertThat(resultado.hashRecalculado()).isEqualTo("hash-modificado");
        }
    }

    @Nested
    @DisplayName("obtenerParaPanel()")
    class ObtenerParaPanel {

        @Test
        @DisplayName("Debe retornar todos los expedientes cuando no se especifica filtro de estatus")
        void debeRetornarTodosSinFiltro() {
            when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo));

            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel(null, null);

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("Debe excluir estados inactivos cuando el filtro es ACTIVO")
        void debeFiltrarSoloActivos() {
            Expediente cerrado = Expediente.builder().id(2L).folio("EXP-2026-CCCC3333")
                    .estadoExpediente(EstadoExpediente.CERRADO).build();
            Expediente activo = Expediente.builder().id(3L).folio("EXP-2026-DDDD4444")
                    .estadoExpediente(EstadoExpediente.INVESTIGACION_ACTIVA).build();
            when(expedienteRepository.findAll()).thenReturn(List.of(cerrado, activo));

            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel("ACTIVO", null);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo("3");
        }

        @Test
        @DisplayName("Debe filtrar por un estado específico cuando se indica")
        void debeFiltrarPorEstadoEspecifico() {
            when(expedienteRepository.findByEstadoExpediente(EstadoExpediente.CERRADO))
                    .thenReturn(List.of(expedienteEjemplo));

            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel("cerrado", null);

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("Debe ordenar por folio descendente cuando se indica 'folio,desc'")
        void debeOrdenarPorFolioDescendente() {
            Expediente e1 = Expediente.builder().id(1L).folio("EXP-A").build();
            Expediente e2 = Expediente.builder().id(2L).folio("EXP-B").build();
            when(expedienteRepository.findAll()).thenReturn(List.of(e1, e2));

            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel(null, "folio,desc");

            assertThat(resultado).extracting(ExpedienteActivoResponse::folioCOPP).containsExactly("EXP-B", "EXP-A");
        }

        @Test
        @DisplayName("No debe aplicar orden cuando el campo de sort es desconocido")
        void noDebeOrdenarConCampoDesconocido() {
            when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo));

            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel(null, "campoInexistente");

            assertThat(resultado).hasSize(1);
        }
    }
}
