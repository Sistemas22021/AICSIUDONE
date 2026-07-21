package com.guardia.core.service;
import com.guardia.core.EscenaLiberadaEvent;
import com.guardia.core.HashStrategy;

import com.guardia.core.dto.request.EscenaRequest;
import com.guardia.core.dto.request.LiberarEscenaRequest;
import com.guardia.core.dto.response.EscenaResponse;
import com.guardia.core.dto.response.EscenaNegativaResponse;
import com.guardia.core.dto.response.EvidenciaResponse;
import com.guardia.core.dto.response.EscenaChecklistResponse;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.EscenaChecklist;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.PasoChecklist;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.repository.EscenaChecklistRepository;
import com.guardia.core.service.EscenaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de EscenaService: maneja la lógica de checklist, pasos y estados de escena.
 */
public class EscenaServiceImpl implements EscenaService {

    private final EscenaRepository escenaRepository;
    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EscenaChecklistRepository escenaChecklistRepository;
    private final HashStrategy hashStrategy;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public EscenaResponse crear(EscenaRequest request) {
        Expediente expediente = expedienteRepository.findById(request.expedienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", request.expedienteId()));

        Usuario investigador = usuarioRepository.findById(request.levantadaPorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.levantadaPorId()));

        Escena escena = new Escena();
        escena.setExpediente(expediente);
        escena.setLevantadaPor(investigador);
        escena.setEvidencias(new ArrayList<>());
        escena.setEscenasNegativas(new ArrayList<>());
        escena.setChecklist(crearChecklistInicial(escena));

        escena.setEstadoChecklist("PENDIENTE");
        escena.setPasoActual( PasoChecklist.ASEGURAMIENTO_PERIMETRO);

        return toResponse(escenaRepository.save(escena));
    }

    private List<EscenaChecklist> crearChecklistInicial(Escena escena) {

        List<EscenaChecklist> pasos = new ArrayList<>();

        pasos.add(
                EscenaChecklist.builder()
                        .paso(PasoChecklist.ASEGURAMIENTO_PERIMETRO)
                        .orden(1)
                        .completado(false)
                        .fechaInicio(null)
                        .fechaCierre(null)
                        .escena(escena)
                        .build()
        );

        pasos.add(
                EscenaChecklist.builder()
                        .paso(PasoChecklist.DOCUMENTACION_EVIDENCIA)
                        .orden(2)
                        .completado(false)
                        .fechaInicio(null)
                        .fechaCierre(null)
                        .escena(escena)
                        .build()
        );

        pasos.add(
                EscenaChecklist.builder()
                        .paso(PasoChecklist.RECOLECCION_EMBALAJE)
                        .orden(3)
                        .completado(false)
                        .fechaInicio(null)
                        .fechaCierre(null)
                        .escena(escena)
                        .build()
        );

        pasos.add(
                EscenaChecklist.builder()
                        .paso(PasoChecklist.LIBERACION_ESCENA)
                        .orden(4)
                        .completado(false)
                        .fechaInicio(null)
                        .fechaCierre(null)
                        .escena(escena)
                        .build()
        );

        return pasos;
    }

    private EscenaChecklist obtenerPasoActual(Escena escena) {

        return escenaChecklistRepository
                .findByEscenaIdOrderByOrden(escena.getId())
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getCompletado()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public EscenaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaResponse> obtenerTodos() {
        return escenaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaResponse> obtenerPorExpediente(Long expedienteId) {
        return escenaRepository.findByExpedienteId(expedienteId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaResponse> obtenerPorInvestigador(UUID usuarioId) {
        return escenaRepository.findByLevantadaPorId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        escenaRepository.deleteById(id);
    }

    @Override
    public EscenaResponse avanzarPaso(Long id) {
        Escena escena = findById(id);
        EscenaChecklist pasoActual =
                obtenerPasoActual(escena);
        if (pasoActual == null) {
            throw new BusinessException("Checklist ya completado.");
        }
        if (pasoActual.getPaso() == PasoChecklist.DOCUMENTACION_EVIDENCIA) {

            if (escena.getEvidencias() == null || escena.getEvidencias().isEmpty()) {
                throw new BusinessException("Debe registrar al menos una evidencia antes de continuar.");
            }

            boolean tieneNegativas = escena.getEscenasNegativas() != null
                    && !escena.getEscenasNegativas().isEmpty();
            if (!tieneNegativas) {
                throw new BusinessException(
                        "Debe registrar al menos un elemento de escena negativa, " +
                                "o marcar explícitamente 'sin elementos negativos a reportar'."
                );
            }
        }

        pasoActual.setCompletado(true);
        escena.registrarTimestampPaso(pasoActual, true);
        escenaChecklistRepository.save(pasoActual);
        EscenaChecklist siguientePaso =
                obtenerPasoActual(escena);
        if (siguientePaso != null) {
            if (siguientePaso.getFechaInicio() == null) {
                escena.registrarTimestampPaso(siguientePaso, false);
                escenaChecklistRepository.save(siguientePaso);
            }
            escena.setPasoActual(siguientePaso.getPaso());
        } else {
            escena.setPasoActual(null);
            escena.setEstadoChecklist("COMPLETADO");
            escena.setCierreProceso(LocalDateTime.now());
        }
        return toResponse(
                escenaRepository.save(escena)
        );
    }

    @Override
    public EscenaResponse iniciarChecklist(Long id) {
        Escena escena = findById(id);
        if ("INICIADO".equals(escena.getEstadoChecklist()) || "COMPLETADO".equals(escena.getEstadoChecklist())) {
            throw new BusinessException("La escena ya fue iniciada o completada.");
        }
        escena.iniciarChecklist();
        EscenaChecklist primerPaso = obtenerPasoActual(escena);
        if(primerPaso != null){
            primerPaso.setFechaInicio(LocalDateTime.now());
            escenaChecklistRepository.save(primerPaso);
        }
        return toResponse(escenaRepository.save(escena));
    }

    @Override
    public EscenaResponse cerrar(Long id) {
        Escena escena = findById(id);
        if ("CERRADO".equals(escena.getEstadoChecklist()))
            throw new BusinessException("La escena ya está cerrada.");
        if (!"INICIADO".equals(escena.getEstadoChecklist()))
            throw new BusinessException("La escena debe estar iniciada antes de cerrarla.");
        escena.cerrar();
        return toResponse(escenaRepository.save(escena));
    }

    @Override
    public EscenaResponse bloquearEdicion(Long id) {
        Escena escena = findById(id);
        escena.bloquearEdicion();
        return toResponse(escenaRepository.save(escena));
    }

    @Override
    public EscenaResponse liberar(Long id, LiberarEscenaRequest request) {
        Escena escena = findById(id);

        if (escena.estaLiberada()) {
            throw new BusinessException("La escena ya fue liberada formalmente y su registro está sellado.");
        }

        List<EscenaChecklist> pasosOrdenados =
                escenaChecklistRepository.findByEscenaIdOrderByOrden(id);

        EscenaChecklist pasoLiberacion = pasosOrdenados.stream()
                .filter(p -> p.getPaso() == PasoChecklist.LIBERACION_ESCENA)
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "La escena no tiene configurado el paso de liberación en su checklist."));

        boolean pasosPreviosCompletos = pasosOrdenados.stream()
                .filter(p -> p.getOrden() < pasoLiberacion.getOrden())
                .allMatch(p -> Boolean.TRUE.equals(p.getCompletado()) && p.getFechaCierre() != null);

        if (!pasosPreviosCompletos) {
            throw new BusinessException(
                    "No se puede liberar la escena: los pasos previos del checklist " +
                            "(aseguramiento de perímetro, documentación de evidencia y recolección/embalaje) " +
                            "deben estar completados y firmados."
            );
        }

        Usuario investigador = usuarioRepository.findById(request.investigadorResponsableId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.investigadorResponsableId()));

        LocalDateTime horaCierre = LocalDateTime.now();

        String hash = hashStrategy.calcular(
                id + "|" + investigador.getId() + "|" + investigador.getFullName() + "|" +
                        horaCierre + "|" + (request.observaciones() == null ? "" : request.observaciones())
        );

        escena.liberar(investigador, horaCierre, request.observaciones(), hash);

        pasoLiberacion.setCompletado(true);
        escena.registrarTimestampPaso(pasoLiberacion, true);
        escenaChecklistRepository.save(pasoLiberacion);

        Escena guardada = escenaRepository.save(escena);
        eventPublisher.publishEvent(new EscenaLiberadaEvent(this, guardada));

        return toResponse(guardada);
    }

    @Override
    public boolean validarSecuencia(Long id) {
        return findById(id).validarSecuencia();
    }

    private Escena findById(Long id) {
        return escenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escena", id));
    }

    public EscenaResponse toResponse(Escena e) {
        UsuarioResponse investigador = e.getLevantadaPor() == null ? null :
                new UsuarioResponse(e.getLevantadaPor().getId(), e.getLevantadaPor().getUsername(),
                        e.getLevantadaPor().getFullName(), e.getLevantadaPor().getProfilePhotoUrl());

        Long expedienteId = e.getExpediente() != null ? e.getExpediente().getId() : null;

        List<EvidenciaResponse> evidencias = e.getEvidencias() == null ? List.of() :
                e.getEvidencias().stream()
                .map(ev -> new EvidenciaResponse(
                        ev.getId(),
                        ev.getNumeroItem(),
                        ev.getTipo(),
                        ev.getDescripcion(),
                        e.getId(),
                        ev.getHashIntegridad(),
                        ev.getTimestampRegistro(),
                        ev.getInvestigador() != null ? ev.getInvestigador().getFullName() : null
                ))
                .toList();


        List<EscenaNegativaResponse> negativas = e.getEscenasNegativas() == null ? List.of() :
                e.getEscenasNegativas().stream()
                        .map(en -> new EscenaNegativaResponse(en.getId(), en.getElementoBuscado(),
                                en.getAreaInspeccionada(), en.getResultado(), en.getObservacion(), e.getId(), en.getSinElementosNegativos()))
                        .toList();

        UsuarioResponse liberadaPor = e.getLiberadaPor() == null ? null :
                new UsuarioResponse(e.getLiberadaPor().getId(), e.getLiberadaPor().getUsername(),
                        e.getLiberadaPor().getFullName(), e.getLiberadaPor().getProfilePhotoUrl());

        return new EscenaResponse(
                e.getId(),
                e.getEstadoChecklist(),
                e.getEstado() != null ? e.getEstado().name() : null,
                e.getPasoActual() != null ? e.getPasoActual().name() : null,
                e.getInicioProceso(),
                e.getCierreProceso(),
                expedienteId,
                investigador,
                evidencias,
                negativas,
                liberadaPor,
                e.getHoraLiberacion(),
                e.getObservacionesLiberacion(),
                e.getHashLiberacion()
        );
    }
    @Override
    @Transactional(readOnly = true)
    public List<EscenaChecklistResponse> obtenerChecklist(Long id) {
        findById(id);
        return escenaChecklistRepository
                .findByEscenaIdOrderByOrden(id)
                .stream()
                .map(p -> new EscenaChecklistResponse(
                        p.getId(),
                        p.getPaso(),
                        p.getOrden(),
                        p.getCompletado(),
                        p.getFechaInicio(),
                        p.getFechaCierre()
                ))
                .toList();
    }
}
