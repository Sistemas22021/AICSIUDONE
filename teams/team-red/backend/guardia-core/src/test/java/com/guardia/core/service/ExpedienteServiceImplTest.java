// Ruta destino: src/test/java/com/guardia/core/service/ExpedienteServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.ExpedienteRegistradoEvent;
import com.guardia.core.SelloExpedienteEvent;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link ExpedienteServiceImpl}.
 * Es el servicio central del dominio: cubre creación, sellado, verificación de integridad
 * y las reglas de negocio que protegen expedientes ya sellados o archivados.
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
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común)
        usuarioEjemplo = Usuario.builder().id(1L).nombre("Agente Gómez").identificacion("V-1").correo("g@x.com").build();

        expedienteEjemplo = Expediente.builder()
                .id(1L)
                .folio("EXP-2026-AAAA1111")
                .estadoExpediente(EstadoExpediente.BORRADOR)
                .fechaCreacion(LocalDateTime.now())
                .descripcionHecho("Descripción del hecho")
                .involucrados(new ArrayList<>())
                .escenas(new ArrayList<>())
                .modusOperandiList(new ArrayList<>())
                .delitos(new ArrayList<>())
                .build();
    }

    /** Construye un ExpedienteRequest válido y mínimo (ubicación + 1 delito + 1 víctima). */
    private ExpedienteRequest requestMinimo() {
        ExpedienteRequest request = new ExpedienteRequest();

        UbicacionRequest ubicacion = new UbicacionRequest();
        ubicacion.setMunicipio("Libertador");
        ubicacion.setSector("Catia");
        ubicacion.setDireccion("Av. Principal");
        ubicacion.setReferencia("Cerca de la plaza");
        CoordenadasRequest coords = new CoordenadasRequest();
        coords.setLatitud(10.5);
        coords.setLongitud(-66.9);
        ubicacion.setCoordenadas(coords);
        request.setUbicacion(ubicacion);

        request.setDescripcion("Descripción del hecho");

        DelitoRequest delito = new DelitoRequest();
        delito.setDelito("HOMICIDIO");
        SubDelitoRequest subDelito = new SubDelitoRequest();
        subDelito.setNombre("HOMICIDIO_CULPOSO");
        delito.setSubDelito(subDelito);
        delito.setFechaHecho(LocalDate.of(2026, 1, 15));
        delito.setHoraInicioHecho(LocalTime.of(10, 30));
        delito.setHechoEnCurso(false);
        request.setDelitos(List.of(delito));

        InvolucradosRequest victima = new InvolucradosRequest();
        victima.setNombre("Maria Lopez");
        victima.setCedula("V-9999999");
        victima.setTelefono("0414-1234567");
        request.setVictimas(List.of(victima));

        return request;
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear el expediente completo con ubicación, delito, víctima y publicar el evento de registro")
        void debeCrearExpedienteCompleto() {
            // Arrange
            ExpedienteRequest request = requestMinimo();
            TipoDelito tipoDelito = TipoDelito.builder().id(5L).nombre("HOMICIDIO").requiereSubtipo(true).build();
            SubtipoDelito subtipo = SubtipoDelito.builder().id(50L).nombre("HOMICIDIO_CULPOSO").tipoDelito(tipoDelito).build();

            when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tipoDelitoRepository.findByNombre("HOMICIDIO")).thenReturn(Optional.of(tipoDelito));
            when(subtipoDelitoRepository.findByTipoDelitoId(5L)).thenReturn(List.of(subtipo));
            // Simula la asignación de id que realizaría JPA al persistir (IDENTITY strategy)
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> {
                Expediente e = inv.getArgument(0);
                e.setId(100L);
                return e;
            });

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert
            assertThat(resultado.folio()).startsWith("EXP-2026-");
            assertThat(resultado.estadoExpediente()).isEqualTo(EstadoExpediente.BORRADOR);
            assertThat(resultado.involucrados()).hasSize(1);
            assertThat(resultado.involucrados().get(0).nombre()).isEqualTo("Maria Lopez");
            assertThat(resultado.localizacion().municipio()).isEqualTo("Libertador");

            ArgumentCaptor<ExpedienteRegistradoEvent> eventCaptor = ArgumentCaptor.forClass(ExpedienteRegistradoEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().getExpedienteId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Debe incluir al denunciante como involucrado cuando viene informado en la solicitud")
        void debeIncluirDenuncianteComoInvolucrado() {
            // Arrange
            ExpedienteRequest request = requestMinimo();
            InvolucradosRequest denunciante = new InvolucradosRequest();
            denunciante.setNombre("Pedro Gomez");
            denunciante.setCedula("V-1111111");
            request.setDenunciante(denunciante);

            when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tipoDelitoRepository.findByNombre("HOMICIDIO")).thenReturn(Optional.empty());
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert: denunciante + víctima = 2 involucrados
            assertThat(resultado.involucrados()).hasSize(2);
            assertThat(resultado.involucrados()).extracting("nombre")
                    .contains("Pedro Gomez", "Maria Lopez");
        }

        @Test
        @DisplayName("No debe crear ni guardar localización cuando la ubicación no viene informada")
        void noDebeCrearLocalizacionCuandoUbicacionEsNula() {
            // Arrange
            ExpedienteRequest request = requestMinimo();
            request.setUbicacion(null);
            when(tipoDelitoRepository.findByNombre("HOMICIDIO")).thenReturn(Optional.empty());
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert
            assertThat(resultado.localizacion()).isNull();
            verify(localizacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("No debe promover subtipo cuando el tipo de delito no fue encontrado por nombre")
        void noDebePromoverSubtipoCuandoTipoDelitoNoExiste() {
            // Arrange
            ExpedienteRequest request = requestMinimo();
            when(localizacionRepository.save(any(Localizacion.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tipoDelitoRepository.findByNombre("HOMICIDIO")).thenReturn(Optional.empty());
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.crear(request);

            // Assert
            assertThat(resultado.tipoDelito()).isNull();
            assertThat(resultado.subtipoDelito()).isNull();
            verify(subtipoDelitoRepository, never()).findByTipoDelitoId(any());
        }
    }

    @Nested
    @DisplayName("obtenerPorId() / obtenerPorFolio()")
    class Consultas {

        @Test
        @DisplayName("obtenerPorId() debe retornar el expediente cuando existe")
        void debeRetornarExpedientePorId() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act
            ExpedienteResponse resultado = expedienteService.obtenerPorId(1L);

            // Assert
            assertThat(resultado.folio()).isEqualTo("EXP-2026-AAAA1111");
        }

        @Test
        @DisplayName("obtenerPorId() debe lanzar ResourceNotFoundException cuando no existe")
        void debeLanzarExcepcionPorIdInexistente() {
            // Arrange
            when(expedienteRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("obtenerPorFolio() debe retornar el expediente cuando el folio existe")
        void debeRetornarExpedientePorFolio() {
            // Arrange
            when(expedienteRepository.findByFolio("EXP-2026-AAAA1111")).thenReturn(Optional.of(expedienteEjemplo));

            // Act
            ExpedienteResponse resultado = expedienteService.obtenerPorFolio("EXP-2026-AAAA1111");

            // Assert
            assertThat(resultado.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("obtenerPorFolio() debe lanzar ResourceNotFoundException con mensaje específico cuando no existe")
        void debeLanzarExcepcionPorFolioInexistente() {
            // Arrange
            when(expedienteRepository.findByFolio("NOEXISTE")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.obtenerPorFolio("NOEXISTE"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Expediente con folio NOEXISTE no encontrado.");
        }
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todos los expedientes mapeados")
    void debeRetornarTodosLosExpedientes() {
        // Arrange
        when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo));

        // Act
        List<ExpedienteResponse> resultado = expedienteService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorEstado() debe filtrar por el estado indicado")
    void debeRetornarExpedientesPorEstado() {
        // Arrange
        when(expedienteRepository.findByEstadoExpediente(EstadoExpediente.BORRADOR))
                .thenReturn(List.of(expedienteEjemplo));

        // Act
        List<ExpedienteResponse> resultado = expedienteService.obtenerPorEstado(EstadoExpediente.BORRADOR);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorCreador() debe filtrar por el usuario creador")
    void debeRetornarExpedientesPorCreador() {
        // Arrange
        when(expedienteRepository.findByCreadoPorId(1L)).thenReturn(List.of(expedienteEjemplo));

        // Act
        List<ExpedienteResponse> resultado = expedienteService.obtenerPorCreador(1L);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar descripción y fecha del hecho cuando el expediente está en BORRADOR")
        void debeActualizarExpedienteEnBorrador() {
            // Arrange
            ExpedienteRequest request = requestMinimo();
            request.setDescripcion("Nueva descripción");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.actualizar(1L, request);

            // Assert
            assertThat(resultado.descripcionHecho()).isEqualTo("Nueva descripción");
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el expediente ya está sellado")
        void debeLanzarExcepcionCuandoEstaSellado() {
            // Arrange
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.PROCESADO_Y_SELLADO);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.actualizar(1L, requestMinimo()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("No se puede modificar un expediente sellado o archivado.");
            verify(expedienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el expediente está archivado")
        void debeLanzarExcepcionCuandoEstaArchivado() {
            // Arrange
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.ARCHIVADO);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.actualizar(1L, requestMinimo()))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el expediente cuando está en estado BORRADOR")
        void debeEliminarExpedienteEnBorrador() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act
            expedienteService.eliminar(1L);

            // Assert
            verify(expedienteRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el estado no es BORRADOR")
        void debeLanzarExcepcionCuandoNoEsBorrador() {
            // Arrange
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.EN_REVISION);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act & Assert
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
        @DisplayName("Debe sellar el expediente cuando los datos son válidos y el agente existe")
        void debeSellarExpedienteExitosamente() {
            // Arrange: datos suficientes para validarDatos() == true
            expedienteEjemplo.setTipoDelito(TipoDelito.builder().id(1L).nombre("HOMICIDIO").build());
            expedienteEjemplo.setMunicipio("Libertador");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.sellar(1L, 1L);

            // Assert
            assertThat(resultado).isNotNull();
            verify(selloStrategy).aplicar(eq(expedienteEjemplo), eq(usuarioEjemplo), any(LocalDateTime.class));
            verify(eventPublisher).publishEvent(any(SelloExpedienteEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el expediente ya está sellado")
        void debeLanzarExcepcionCuandoYaSellado() {
            // Arrange
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.PROCESADO_Y_SELLADO);
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.sellar(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("El expediente ya está sellado.");
            verify(selloStrategy, never()).aplicar(any(), any(), any());
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando los datos del expediente están incompletos")
        void debeLanzarExcepcionCuandoDatosIncompletos() {
            // Arrange: sin tipoDelito ni municipio => validarDatos() == false
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.sellar(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("El expediente no tiene todos los datos requeridos para sellarse.");
            verify(usuarioRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el agente sellador no existe")
        void debeLanzarExcepcionCuandoAgenteNoExiste() {
            // Arrange
            expedienteEjemplo.setTipoDelito(TipoDelito.builder().id(1L).nombre("HOMICIDIO").build());
            expedienteEjemplo.setMunicipio("Libertador");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.sellar(1L, 77L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(selloStrategy, never()).aplicar(any(), any(), any());
        }
    }

    @Test
    @DisplayName("cambiarEstado() debe delegar en la entidad y persistir el nuevo estado")
    void debeCambiarEstado() {
        // Arrange
        when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
        when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ExpedienteResponse resultado = expedienteService.cambiarEstado(1L, EstadoExpediente.EN_REVISION);

        // Assert
        assertThat(resultado.estadoExpediente()).isEqualTo(EstadoExpediente.EN_REVISION);
    }

    @Nested
    @DisplayName("asignarInvestigador()")
    class AsignarInvestigador {

        @Test
        @DisplayName("Debe asignar el investigador y cambiar el estado a ASIGNADO_A_EQUIPO")
        void debeAsignarInvestigadorExitosamente() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.asignarInvestigador(1L, 1L);

            // Assert
            assertThat(resultado.estadoExpediente()).isEqualTo(EstadoExpediente.ASIGNADO_A_EQUIPO);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el investigador no existe")
        void debeLanzarExcepcionCuandoInvestigadorNoExiste() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.asignarInvestigador(1L, 77L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(expedienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("vincularEscena()")
    class VincularEscena {

        @Test
        @DisplayName("Debe vincular la escena al expediente cuando ambos existen")
        void debeVincularEscenaExitosamente() {
            // Arrange
            Escena escena = Escena.builder().id(5L).build();
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(escenaRepository.findById(5L)).thenReturn(Optional.of(escena));
            when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            ExpedienteResponse resultado = expedienteService.vincularEscena(1L, 5L);

            // Assert
            assertThat(resultado.escenas()).hasSize(1);
            assertThat(escena.getExpediente()).isEqualTo(expedienteEjemplo);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la escena no existe")
        void debeLanzarExcepcionCuandoEscenaNoExiste() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(escenaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> expedienteService.vincularEscena(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("asignarFechaHecho() debe parsear el ISO-8601 y actualizar la fecha del hecho")
    void debeAsignarFechaHecho() {
        // Arrange
        when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
        when(expedienteRepository.save(any(Expediente.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ExpedienteResponse resultado = expedienteService.asignarFechaHecho(1L, "2026-01-15T10:30:00");

        // Assert
        assertThat(resultado.fechaHecho()).isEqualTo(LocalDateTime.of(2026, 1, 15, 10, 30, 0));
    }

    @Nested
    @DisplayName("validarDatos()")
    class ValidarDatos {

        @Test
        @DisplayName("Debe retornar true cuando descripción, tipo de delito y localización están completos")
        void debeRetornarTrueCuandoDatosCompletos() {
            // Arrange
            expedienteEjemplo.setTipoDelito(TipoDelito.builder().id(1L).build());
            expedienteEjemplo.setLocalizacion(Localizacion.builder().id(1L).build());
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act
            boolean resultado = expedienteService.validarDatos(1L);

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el tipo de delito")
        void debeRetornarFalseCuandoFaltaTipoDelito() {
            // Arrange
            expedienteEjemplo.setLocalizacion(Localizacion.builder().id(1L).build());
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act
            boolean resultado = expedienteService.validarDatos(1L);

            // Assert
            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("verificarIntegridad()")
    class VerificarIntegridad {

        @Test
        @DisplayName("Debe indicar que no ha sido sellado cuando no tiene hash de integridad")
        void debeIndicarQueNoHaSidoSellado() {
            // Arrange
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));

            // Act
            VerificacionHashResponse resultado = expedienteService.verificarIntegridad(1L);

            // Assert
            assertThat(resultado.integro()).isFalse();
            assertThat(resultado.mensaje()).isEqualTo("El expediente no ha sido sellado.");
        }

        @Test
        @DisplayName("Debe reportar íntegro cuando el hash recalculado coincide con el almacenado")
        void debeReportarIntegroCuandoHashCoincide() {
            // Arrange
            expedienteEjemplo.setHashIntegridad("hash-original");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(selloStrategy.recalcularHash(expedienteEjemplo)).thenReturn("hash-original");

            // Act
            VerificacionHashResponse resultado = expedienteService.verificarIntegridad(1L);

            // Assert
            assertThat(resultado.integro()).isTrue();
            assertThat(resultado.mensaje()).contains("no fue alterado");
        }

        @Test
        @DisplayName("Debe reportar alerta cuando el hash recalculado difiere del almacenado")
        void debeReportarAlertaCuandoHashDifiere() {
            // Arrange
            expedienteEjemplo.setHashIntegridad("hash-original");
            when(expedienteRepository.findById(1L)).thenReturn(Optional.of(expedienteEjemplo));
            when(selloStrategy.recalcularHash(expedienteEjemplo)).thenReturn("hash-modificado");

            // Act
            VerificacionHashResponse resultado = expedienteService.verificarIntegridad(1L);

            // Assert
            assertThat(resultado.integro()).isFalse();
            assertThat(resultado.mensaje()).contains("ALERTA");
        }
    }

    @Nested
    @DisplayName("obtenerParaPanel()")
    class ObtenerParaPanel {

        @Test
        @DisplayName("Debe retornar todos los expedientes cuando no se indica estatus")
        void debeRetornarTodosSinFiltroDeEstatus() {
            // Arrange
            when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo));

            // Act
            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel(null, null);

            // Assert
            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("Debe excluir estados inactivos cuando el estatus es ACTIVO")
        void debeExcluirEstadosInactivosParaEstatusActivo() {
            // Arrange: un expediente en estado activo (EN_REVISION) y otro inactivo (CERRADO)
            expedienteEjemplo.setEstadoExpediente(EstadoExpediente.EN_REVISION);
            Expediente cerrado = Expediente.builder().id(2L).folio("EXP-2026-BBBB2222")
                    .estadoExpediente(EstadoExpediente.CERRADO).build();
            when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo, cerrado));

            // Act
            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel("ACTIVO", null);

            // Assert: el expediente activo se incluye, el cerrado (inactivo) se excluye
            assertThat(resultado).extracting(ExpedienteActivoResponse::folioCOPP)
                    .containsExactly("EXP-2026-AAAA1111")
                    .doesNotContain("EXP-2026-BBBB2222");
        }

        @Test
        @DisplayName("Debe filtrar por el estado exacto indicado en el estatus")
        void debeFiltrarPorEstadoExacto() {
            // Arrange
            when(expedienteRepository.findByEstadoExpediente(EstadoExpediente.EN_REVISION))
                    .thenReturn(List.of(expedienteEjemplo));

            // Act
            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel("EN_REVISION", null);

            // Assert
            assertThat(resultado).hasSize(1);
            verify(expedienteRepository).findByEstadoExpediente(EstadoExpediente.EN_REVISION);
        }

        @Test
        @DisplayName("Debe ordenar de forma descendente por folio cuando sort=folio,desc")
        void debeOrdenarDescendentePorFolio() {
            // Arrange
            Expediente expedienteB = Expediente.builder().id(2L).folio("EXP-2026-ZZZZ9999")
                    .estadoExpediente(EstadoExpediente.BORRADOR).build();
            when(expedienteRepository.findAll()).thenReturn(List.of(expedienteEjemplo, expedienteB));

            // Act
            List<ExpedienteActivoResponse> resultado = expedienteService.obtenerParaPanel(null, "folio,desc");

            // Assert
            assertThat(resultado).extracting(ExpedienteActivoResponse::folioCOPP)
                    .containsExactly("EXP-2026-ZZZZ9999", "EXP-2026-AAAA1111");
        }
    }
}
