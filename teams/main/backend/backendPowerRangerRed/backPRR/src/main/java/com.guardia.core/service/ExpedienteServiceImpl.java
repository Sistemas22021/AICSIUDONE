package com.guardia.core.service;

import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.request.DelitoRequest;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.dto.response.TipoDelitoResponse;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.dto.response.InvolucradoResponse;
import com.guardia.core.dto.response.LocalizacionResponse;
import com.guardia.core.dto.response.EscenaResponse;
import com.guardia.core.dto.response.VerificacionHashResponse;
import com.guardia.core.SelloExpedienteEvent;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Localizacion;
import com.guardia.core.model.Involucrado;
import com.guardia.core.model.DelitoEnExpediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.Escena;
import com.guardia.core.model.enums.EstadoExpediente;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.repository.TipoDelitoRepository;
import com.guardia.core.repository.SubtipoDelitoRepository;
import com.guardia.core.repository.LocalizacionRepository;
import com.guardia.core.repository.InvolucradoRepository;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.SelloStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpedienteServiceImpl implements ExpedienteService {

    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoDelitoRepository tipoDelitoRepository;
    private final SubtipoDelitoRepository subtipoDelitoRepository;
    private final LocalizacionRepository localizacionRepository;
    private final EscenaRepository escenaRepository;
    private final SelloStrategy selloStrategy;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;
    private final InvolucradoRepository involucradoRepository;

    @Override
    public ExpedienteResponse crear(ExpedienteRequest request) {
        // Mapear ubicación
        Localizacion localizacion = new Localizacion();
        if (request.getUbicacion() != null) {
            localizacion.registrarDireccionManual(
                    request.getUbicacion().getMunicipio(),
                    request.getUbicacion().getSector(),
                    request.getUbicacion().getDireccion(),
                    request.getUbicacion().getReferencia()
            );
            localizacion = localizacionRepository.save(localizacion);
        }


        // Crear expediente
        String folio = "EXP-2026-" +
                java.util.UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Expediente expediente = new Expediente();

        expediente.setFolio(folio);
        expediente.setNumeroUnico(folio);
        expediente.setFechaCreacion(LocalDateTime.now());
        expediente.setDescripcionHecho(request.getDescripcion());
        expediente.setLocalizacion(localizacion);

        expediente.setInvolucrados(new ArrayList<>());
        expediente.setEscenas(new ArrayList<>());
        expediente.setModusOperandiList(new ArrayList<>());

        expediente.setEstadoExpediente(
                EstadoExpediente.BORRADOR
        );

        if (request.getDenunciante() != null) {

            Involucrado denunciante = new Involucrado();

            denunciante.setNombre(
                    request.getDenunciante().getNombre()
            );

            denunciante.setIdentificacion(
                    request.getDenunciante().getCedula()
            );

            denunciante.setNumeroTelefono(
                    request.getDenunciante().getNumeroTelefono()
            );

            denunciante.setNacionalidad(
                    request.getDenunciante().getNacionalidad()
            );

            denunciante.setDireccion(
                    request.getDenunciante().getDireccion()
            );

            denunciante.setRelacionConHecho(
                    request.getDenunciante().getRelacionConCrimen()
            );

            denunciante.setRol(
                    TipoRol.DENUNCIANTE
            );

            denunciante.setExpediente(
                    expediente
            );

            expediente.getInvolucrados()
                    .add(denunciante);
        }


        // Mapear delitos (si vienen)
        // Mapear delitos (si vienen)
        if (request.getDelitos() != null && !request.getDelitos().isEmpty()) {
            request.getDelitos().forEach(dReq -> {
                DelitoEnExpediente delito = new DelitoEnExpediente();
                delito.setSubtipoDelito(dReq.getDelito().toUpperCase());
                java.time.LocalDate date = dReq.getFechaHecho();
                java.time.LocalTime time = dReq.getHoraInicioHecho();
                java.time.LocalDateTime fechaHora = java.time.LocalDateTime.of(date, time);
                delito.setFechaHoraHecho(fechaHora);
                expediente.getDelitos().add(delito);
            });

            // Promover el primer delito a la FK directa del expediente
            DelitoRequest primero = request.getDelitos().get(0);
            tipoDelitoRepository.findByNombre(primero.getDelito())
                    .ifPresent(expediente::setTipoDelito);

            // Promover subtipo si el tipoDelito fue encontrado y tiene subDelito
            if (expediente.getTipoDelito() != null && primero.getSubDelito() != null) {
                String nombreSubtipo = primero.getSubDelito().getNombre();
                if (nombreSubtipo != null && !nombreSubtipo.isBlank()) {
                    subtipoDelitoRepository
                            .findByTipoDelitoId(expediente.getTipoDelito().getId())
                            .stream()
                            .filter(s -> s.getNombre().equalsIgnoreCase(nombreSubtipo))
                            .findFirst()
                            .ifPresent(expediente::setSubtipoDelito);
                }
            }
        }

        // Mapear víctimas
        if (request.getVictimas() != null) {

            request.getVictimas().forEach(vReq -> {

                Involucrado victima = new Involucrado();

                victima.setNombre(
                        vReq.getNombre()
                );

                victima.setIdentificacion(
                        vReq.getCedula()
                );

                victima.setNumeroTelefono(
                        vReq.getTelefono()
                );

                victima.setNacionalidad(
                        vReq.getNacionalidad()
                );

                victima.setDireccion(
                        vReq.getDireccion()
                );

                victima.setRol(
                        TipoRol.VICTIMA
                );

                victima.setExpediente(
                        expediente
                );

                expediente.getInvolucrados()
                        .add(victima);
            });
        }

        return toResponse(expedienteRepository.save(expediente));
    }

    @Override
    @Transactional(readOnly = true)
    public ExpedienteResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ExpedienteResponse obtenerPorFolio(String folio) {
        return expedienteRepository.findByFolio(folio)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Expediente con folio " + folio + " no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpedienteResponse> obtenerTodos() {
        return expedienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpedienteResponse> obtenerPorEstado(EstadoExpediente estado) {
        return expedienteRepository.findByEstadoExpediente(estado).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpedienteResponse> obtenerPorCreador(Long usuarioId) {
        return expedienteRepository.findByCreadoPorId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Override
    public ExpedienteResponse actualizar(Long id, ExpedienteRequest request) {
        Expediente expediente = findById(id);
        if (expediente.getEstadoExpediente() == EstadoExpediente.PROCESADO_Y_SELLADO
                || expediente.getEstadoExpediente() == EstadoExpediente.ARCHIVADO)
            throw new BusinessException("No se puede modificar un expediente sellado o archivado.");

        expediente.setDescripcionHecho(request.getDescripcion());
        if (request.getDelitos() != null && !request.getDelitos().isEmpty()) {
            var dReq = request.getDelitos().get(0);
            java.time.LocalDate date = dReq.getFechaHecho();
            java.time.LocalTime time = dReq.getHoraInicioHecho();
            expediente.setFechaHecho(java.time.LocalDateTime.of(date, time));
        }
        return toResponse(expedienteRepository.save(expediente));
    }

    @Override
    public void eliminar(Long id) {
        Expediente expediente = findById(id);
        if (expediente.getEstadoExpediente() != EstadoExpediente.BORRADOR)
            throw new BusinessException("Solo se pueden eliminar expedientes en estado BORRADOR.");
        expedienteRepository.deleteById(id);
    }

    @Override
    public ExpedienteResponse sellar(Long id, Long agenteSelladorId) {
        Expediente expediente = findById(id);
        if (expediente.getEstadoExpediente() == EstadoExpediente.PROCESADO_Y_SELLADO)
            throw new BusinessException("El expediente ya está sellado.");
        if (!expediente.validarDatos())
            throw new BusinessException("El expediente no tiene todos los datos requeridos para sellarse.");

        Usuario agente = usuarioRepository.findById(agenteSelladorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", agenteSelladorId));

        selloStrategy.aplicar(expediente, agente, LocalDateTime.now());
        Expediente guardado = expedienteRepository.save(expediente);
        eventPublisher.publishEvent(new SelloExpedienteEvent(this, guardado));

        return toResponse(guardado);
    }

    @Override
    public ExpedienteResponse cambiarEstado(Long id, EstadoExpediente nuevoEstado) {
        Expediente expediente = findById(id);
        expediente.cambiarEstado(nuevoEstado);
        return toResponse(expedienteRepository.save(expediente));
    }

    @Override
    public ExpedienteResponse asignarInvestigador(Long id, Long investigadorId) {
        Expediente expediente = findById(id);
        usuarioRepository.findById(investigadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", investigadorId));
        expediente.cambiarEstado(EstadoExpediente.ASIGNADO_A_EQUIPO);
        return toResponse(expedienteRepository.save(expediente));
    }

    @Override
    public ExpedienteResponse vincularEscena(Long id, Long escenaId) {
        Expediente expediente = findById(id);
        Escena escena = escenaRepository.findById(escenaId)
                .orElseThrow(() -> new ResourceNotFoundException("Escena", escenaId));
        expediente.vincularEscena(escena);
        return toResponse(expedienteRepository.save(expediente));
    }

    @Override
    public ExpedienteResponse asignarFechaHecho(Long id, String fecha) {
        Expediente expediente = findById(id);
        expediente.asignarFechaHecho(LocalDateTime.parse(fecha));
        return toResponse(expedienteRepository.save(expediente));
    }

    @Override
    public boolean validarDatos(Long id) {
        return findById(id).validarDatos();
    }

    private Expediente findById(Long id) {
        return expedienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", id));
    }

    public ExpedienteResponse toResponse(Expediente e) {
        UsuarioResponse creadoPor = e.getCreadoPor() == null ? null :
                new UsuarioResponse(e.getCreadoPor().getId(), e.getCreadoPor().getNombre(),
                        e.getCreadoPor().getIdentificacion(), e.getCreadoPor().getCorreo());

        UsuarioResponse selladoPor = e.getSelladoPor() == null ? null :
                new UsuarioResponse(e.getSelladoPor().getId(), e.getSelladoPor().getNombre(),
                        e.getSelladoPor().getIdentificacion(), e.getSelladoPor().getCorreo());

        TipoDelitoResponse tipoDelito = e.getTipoDelito() == null ? null :
                new TipoDelitoResponse(e.getTipoDelito().getId(), e.getTipoDelito().getNombre(),
                        e.getTipoDelito().getDescripcion(), e.getTipoDelito().getRequiereSubtipo(), List.of());

        SubtipoDelitoResponse subtipoDelito = e.getSubtipoDelito() == null ? null :
                new SubtipoDelitoResponse(e.getSubtipoDelito().getId(), e.getSubtipoDelito().getNombre(),
                        e.getSubtipoDelito().getDescripcion(), e.getTipoDelito().getId(),
                        e.getTipoDelito().getNombre());

        List<InvolucradoResponse> involucrados =
                e.getInvolucrados() == null
                        ? List.of()
                        : e.getInvolucrados().stream()
                        .map(i -> new InvolucradoResponse(
                                i.getId(),
                                i.getNombre(),
                                i.getIdentificacion(),
                                i.getNumeroTelefono(),
                                i.getNacionalidad(),
                                i.getDireccion(),
                                i.getRol(),
                                i.getRelacionConHecho()
                        ))
                        .toList();

        LocalizacionResponse localizacion = e.getLocalizacion() == null ? null :
                new LocalizacionResponse(e.getLocalizacion().getId(), e.getLocalizacion().getMunicipio(),
                        e.getLocalizacion().getSector(), e.getLocalizacion().getDireccion(),
                        e.getLocalizacion().getReferencia(), e.getLocalizacion().getLatitud(),
                        e.getLocalizacion().getLongitud(), e.getLocalizacion().obtenerResumenUbicacion());

        List<EscenaResponse> escenas = e.getEscenas() == null
                ? List.of()
                : e.getEscenas().stream()
                .map(es -> new EscenaResponse(
                        es.getId(),
                        es.getEstadoChecklist(),
                        es.getPasoActual() != null
                        ? es.getPasoActual().name()
                        : null,
                        es.getInicioProceso(),
                        es.getCierreProceso(),
                        e.getId(),
                        null,
                        List.of(),
                        List.of()
                ))
                .toList();
        return new ExpedienteResponse(e.getId(), e.getFolio(), e.getEstadoExpediente(),
                e.getFechaCreacion(), e.getFechaSellado(), e.getDescripcionHecho(),
                e.getFechaHecho(), creadoPor, selladoPor, tipoDelito, subtipoDelito,
                localizacion, escenas, involucrados, e.getHashIntegridad(), e.getAgenteSelladorInfo());
    }
    @Override
    @Transactional(readOnly = true)
    public VerificacionHashResponse verificarIntegridad(Long id) {
        Expediente expediente = findById(id);
        if (expediente.getHashIntegridad() == null)
            return new VerificacionHashResponse(id, expediente.getFolio(), false,
                    "El expediente no ha sido sellado.", null, null);

        String recalculado = selloStrategy.recalcularHash(expediente);
        boolean integro = expediente.getHashIntegridad().equals(recalculado);

        return new VerificacionHashResponse(id, expediente.getFolio(), integro,
                integro ? "Integridad verificada: el expediente no fue alterado."
                        : "⚠ ALERTA: discrepancia detectada. El expediente fue modificado.",
                expediente.getHashIntegridad(), recalculado);
    }
}
