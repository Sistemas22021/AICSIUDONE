package com.guardia.core.service;

import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.response.*;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.*;
import com.guardia.core.model.enums.EstadoExpediente;
import com.guardia.core.repository.*;
import com.guardia.core.service.ExpedienteService;
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
    private final DenuncianteRepository denuncianteRepository;
    private final EscenaRepository escenaRepository;

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

        // Mapear denunciante si viene
        Denunciante denunciante = null;
        if (request.getDenunciante() != null) {
            Denunciante d = new Denunciante();
            d.setNombre(request.getDenunciante().getNombre());
            d.setIdentificacion(request.getDenunciante().getCedula());
            d.setDireccion(request.getDenunciante().getDireccion());
            d.setTelefono(request.getDenunciante().getNumeroTelefono());
            d.setNacionalidad(request.getDenunciante().getNacionalidad());
            d.setRelacionConHecho(request.getDenunciante().getRelacionConCrimen());
            denunciante = denuncianteRepository.save(d);
        }

        // Crear expediente
        String folio = "EXP-2026-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Expediente expediente = new Expediente();
        expediente.setFolio(folio);
        expediente.setNumeroUnico(folio);
        expediente.setFechaCreacion(LocalDateTime.now());
        expediente.setDescripcionHecho(request.getDescripcion());
        expediente.setLocalizacion(localizacion);
        expediente.setDenunciante(denunciante);
        expediente.setEscenas(new ArrayList<>());
        expediente.setVictimas(new ArrayList<>());
        expediente.setModusOperandiList(new ArrayList<>());
        expediente.setEstadoExpediente(EstadoExpediente.BORRADOR);

        // Mapear delitos (si vienen)
        if (request.getDelitos() != null) {
            request.getDelitos().forEach(dReq -> {
                DelitoEnExpediente delito = new DelitoEnExpediente();
                delito.setSubtipoDelito(dReq.getDelito().toUpperCase());
                java.time.LocalDate date = dReq.getFechaHecho();
                java.time.LocalTime time = dReq.getHoraInicioHecho();
                java.time.LocalDateTime fechaHora = java.time.LocalDateTime.of(date, time);
                delito.setFechaHoraHecho(fechaHora);
                expediente.getDelitos().add(delito);
            });
        }

        // Mapear víctimas
        if (request.getVictimas() != null) {
            request.getVictimas().forEach(vReq -> {
                Victima v = new Victima();
                v.setNombre(vReq.getNombre());
                v.setIdentificacion(vReq.getCedula());
                v.setTelefono(vReq.getTelefono());
                v.setNacionalidad(vReq.getNacionalidad());
                v.setDireccion(vReq.getDireccion());
                v.setExpediente(expediente);
                expediente.getVictimas().add(v);
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

        expediente.sellarUsuario(agente);
        return toResponse(expedienteRepository.save(expediente));
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

        DenuncianteResponse denunciante = e.getDenunciante() == null ? null :
                new DenuncianteResponse(e.getDenunciante().getId(), e.getDenunciante().getNombre(),
                        e.getDenunciante().getIdentificacion(), e.getDenunciante().getTelefono(),
                        e.getDenunciante().getNacionalidad(), e.getDenunciante().getDireccion(),
                        e.getDenunciante().getRelacionConHecho());

        LocalizacionResponse localizacion = e.getLocalizacion() == null ? null :
                new LocalizacionResponse(e.getLocalizacion().getId(), e.getLocalizacion().getMunicipio(),
                        e.getLocalizacion().getSector(), e.getLocalizacion().getDireccion(),
                        e.getLocalizacion().getReferencia(), e.getLocalizacion().getLatitud(),
                        e.getLocalizacion().getLongitud(), e.getLocalizacion().obtenerResumenUbicacion());

        List<EscenaResponse> escenas = e.getEscenas() == null ? List.of() :
                e.getEscenas().stream().map(es -> new EscenaResponse(es.getId(), es.getEstadoChecklist(),
                        es.getInicioProceso(), es.getCierreProceso(), e.getId(), null,
                        List.of(), List.of())).toList();

        List<VictimaResponse> victimas = e.getVictimas() == null ? List.of() :
                e.getVictimas().stream().map(v -> new VictimaResponse(v.getId(), v.getNombre(),
                        v.getIdentificacion(), v.getTelefono(), v.getNacionalidad(),
                        v.getDireccion(), e.getId())).toList();

        return new ExpedienteResponse(e.getId(), e.getFolio(), e.getEstadoExpediente(),
                e.getFechaCreacion(), e.getFechaSellado(), e.getDescripcionHecho(),
                e.getFechaHecho(), creadoPor, selladoPor, tipoDelito, subtipoDelito,
                denunciante, localizacion, escenas, victimas);
    }
}
