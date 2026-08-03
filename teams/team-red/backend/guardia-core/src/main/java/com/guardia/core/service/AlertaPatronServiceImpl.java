package com.guardia.core.service;

import com.guardia.core.dto.request.DescartarAlertaRequest;
import com.guardia.core.dto.request.RevisarAlertaRequest;
import com.guardia.core.dto.response.AlertaPatronResponse;
import com.guardia.core.dto.response.ExpedienteSimilarResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.AlertaPatron;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.ExpedienteSimilarMO;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoAlerta;
import com.guardia.core.repository.AlertaPatronRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import com.guardia.core.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertaPatronServiceImpl implements AlertaPatronService {

    private static final Logger log = LoggerFactory.getLogger(AlertaPatronServiceImpl.class);

    private static final int MINIMO_EXPEDIENTES_RELACIONADOS = 2;

    private final AlertaPatronRepository alertaPatronRepository;
    private final PropuestaModusOperandiRepository propuestaRepository;
    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${zac.alerta.umbral-confianza-minima:75.0}")
    private double umbralConfianzaMinima;

    @Value("${zac.alerta.ventana-deduplicacion-horas:24}")
    private long ventanaDeduplicacionHoras;

    @Override
    @Async
    public void evaluarYGenerarAlerta(Long propuestaModusOperandiId) {
        try {
            PropuestaModusOperandi propuesta = propuestaRepository.findById(propuestaModusOperandiId)
                    .orElse(null);
            if (propuesta == null) {
                log.warn("[ALERTA] Propuesta MO {} no encontrada; no se evalúa alerta.", propuestaModusOperandiId);
                return;
            }

            List<ExpedienteSimilarMO> relacionados = propuesta.getExpedientesSimilares();
            if (relacionados == null || relacionados.size() < MINIMO_EXPEDIENTES_RELACIONADOS) {
                log.debug("[ALERTA] Propuesta {} tiene menos de {} expedientes relacionados; no se genera alerta.",
                        propuestaModusOperandiId, MINIMO_EXPEDIENTES_RELACIONADOS);
                return;
            }

            double confianza = propuesta.getNivelConfianza() == null ? 0.0 : propuesta.getNivelConfianza();
            if (confianza < umbralConfianzaMinima) {
                log.debug("[ALERTA] Propuesta {} tiene confianza {} por debajo del umbral {}; no se genera alerta.",
                        propuestaModusOperandiId, confianza, umbralConfianzaMinima);
                return;
            }

            Expediente origen = propuesta.getExpediente();
            String clave = construirClaveDeduplicacion(origen.getId(), relacionados);

            LocalDateTime desde = LocalDateTime.now().minusHours(ventanaDeduplicacionHoras);
            if (alertaPatronRepository.existsByClaveDeduplicacionAndFechaGeneracionAfter(clave, desde)) {
                log.info("[ALERTA] Ya existe una alerta para el conjunto de expedientes [{}] en las últimas {}h; se omite duplicado.",
                        clave, ventanaDeduplicacionHoras);
                return;
            }

            List<UUID> investigadores = resolverInvestigadoresNotificados(origen, relacionados);

            AlertaPatron alerta = AlertaPatron.builder()
                    .expedienteOrigen(origen)
                    .propuestaOrigen(propuesta)
                    .expedientesRelacionados(new ArrayList<>(relacionados))
                    .resumenPatron(propuesta.getResumenGenerado())
                    .nivelConfianza(confianza)
                    .estado(EstadoAlerta.PENDIENTE)
                    .claveDeduplicacion(clave)
                    .fechaGeneracion(LocalDateTime.now())
                    .investigadoresNotificados(investigadores)
                    .build();

            alertaPatronRepository.save(alerta);

            log.info("[ALERTA] Generada alerta para expediente origen {} con {} expedientes relacionados, confianza {} y {} destinatarios.",
                    origen.getFolio(), relacionados.size(), confianza, investigadores.size());
        } catch (Exception ex) {
            log.error("[ALERTA] Error evaluando/generando alerta para propuesta id={}", propuestaModusOperandiId, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaPatronResponse> listarTodas() {
        return alertaPatronRepository.findAllByOrderByFechaGeneracionDesc().stream()
                .sorted(Comparator.comparingInt(AlertaPatronServiceImpl::pesoOrdenPendiente))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaPatronResponse> listarPorInvestigador(UUID investigadorId) {
        if (!usuarioRepository.existsById(investigadorId)) {
            throw new ResourceNotFoundException("Usuario", investigadorId);
        }
        return alertaPatronRepository.findByInvestigadorNotificado(investigadorId).stream()
                .sorted(Comparator.comparingInt(AlertaPatronServiceImpl::pesoOrdenPendiente))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AlertaPatronResponse marcarRevisada(Long alertaId, RevisarAlertaRequest request) {
        AlertaPatron alerta = findById(alertaId);
        verificarPendiente(alerta);
        Usuario usuario = findUsuario(request.usuarioId());

        alerta.setEstado(EstadoAlerta.REVISADA);
        alerta.setAtendidaPor(usuario);
        alerta.setFechaAtencion(LocalDateTime.now());

        return toResponse(alertaPatronRepository.save(alerta));
    }

    @Override
    public AlertaPatronResponse marcarDescartada(Long alertaId, DescartarAlertaRequest request) {
        AlertaPatron alerta = findById(alertaId);
        verificarPendiente(alerta);
        Usuario usuario = findUsuario(request.usuarioId());

        alerta.setEstado(EstadoAlerta.DESCARTADA);
        alerta.setAtendidaPor(usuario);
        alerta.setFechaAtencion(LocalDateTime.now());
        alerta.setMotivoDescarte(request.motivo());

        return toResponse(alertaPatronRepository.save(alerta));
    }

    /** Misma clave para el mismo conjunto de expedientes, sin importar el orden en que se recuperaron. */
    private String construirClaveDeduplicacion(Long expedienteOrigenId, List<ExpedienteSimilarMO> relacionados) {
        return Stream.concat(
                        Stream.of(expedienteOrigenId),
                        relacionados.stream().map(ExpedienteSimilarMO::getExpedienteId))
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
    }

    private List<UUID> resolverInvestigadoresNotificados(Expediente origen, List<ExpedienteSimilarMO> relacionados) {
        Set<UUID> investigadores = new LinkedHashSet<>();
        if (origen.getCreadoPor() != null) {
            investigadores.add(origen.getCreadoPor().getId());
        }
        List<Long> idsRelacionados = relacionados.stream().map(ExpedienteSimilarMO::getExpedienteId).toList();
        investigadores.addAll(expedienteRepository.findCreadoPorIdsByIdIn(idsRelacionados));
        return new ArrayList<>(investigadores);
    }

    /** Las alertas pendientes se ordenan primero; dentro de cada grupo se conserva el orden por fecha desc (sort estable). */
    private static int pesoOrdenPendiente(AlertaPatron alerta) {
        return alerta.getEstado() == EstadoAlerta.PENDIENTE ? 0 : 1;
    }

    private void verificarPendiente(AlertaPatron alerta) {
        if (alerta.getEstado() != EstadoAlerta.PENDIENTE) {
            throw new BusinessException(
                    "La alerta " + alerta.getId() + " ya fue atendida (" + alerta.getEstado() + ") y no puede modificarse.");
        }
    }

    private AlertaPatron findById(Long alertaId) {
        return alertaPatronRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("AlertaPatron", alertaId));
    }

    private Usuario findUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
    }

    private AlertaPatronResponse toResponse(AlertaPatron alerta) {
        List<ExpedienteSimilarResponse> relacionados = alerta.getExpedientesRelacionados() == null ? List.of() :
                alerta.getExpedientesRelacionados().stream()
                .map(s -> new ExpedienteSimilarResponse(s.getExpedienteId(), s.getFolio(), s.getSimilitudPorcentaje()))
                .toList();

        Usuario atendidaPor = alerta.getAtendidaPor();

        return new AlertaPatronResponse(
                alerta.getId(),
                alerta.getExpedienteOrigen() != null ? alerta.getExpedienteOrigen().getId() : null,
                alerta.getExpedienteOrigen() != null ? alerta.getExpedienteOrigen().getFolio() : null,
                alerta.getPropuestaOrigen() != null ? alerta.getPropuestaOrigen().getId() : null,
                relacionados,
                alerta.getResumenPatron(),
                alerta.getNivelConfianza(),
                alerta.getEstado(),
                alerta.getFechaGeneracion(),
                atendidaPor != null ? atendidaPor.getId() : null,
                atendidaPor != null ? atendidaPor.getFullName() : null,
                alerta.getFechaAtencion(),
                alerta.getMotivoDescarte());
    }
}