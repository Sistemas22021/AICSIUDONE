package com.guardia.core.service;

import com.guardia.core.dto.request.AprobarPropuestaMoRequest;
import com.guardia.core.dto.request.CorregirPropuestaMoRequest;
import com.guardia.core.dto.request.RechazarPropuestaMoRequest;
import com.guardia.core.dto.response.ExpedienteSimilarResponse;
import com.guardia.core.dto.response.PropuestaModusOperandiResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import com.guardia.core.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de la validación experta del MO (HU3). Todas las acciones
 * quedan registradas con analista, acción, justificación (cuando aplica) y
 * timestamp, y marcan la propuesta como revisada por experto para que HU2
 * no la sobreescriba en corridas futuras del análisis automático.
 */
public class PropuestaModusOperandiServiceImpl implements PropuestaModusOperandiService {

    private final PropuestaModusOperandiRepository propuestaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public PropuestaModusOperandiResponse obtenerVigentePorExpediente(Long expedienteId) {
        PropuestaModusOperandi propuesta = propuestaRepository.findByExpedienteIdAndVigenteTrue(expedienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay una propuesta de Modus Operandi vigente para el expediente " + expedienteId));
        return toResponse(propuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropuestaModusOperandiResponse> historialPorExpediente(Long expedienteId) {
        return propuestaRepository.findByExpedienteIdOrderByVersionDesc(expedienteId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public PropuestaModusOperandiResponse aprobar(Long propuestaId, AprobarPropuestaMoRequest request) {
        PropuestaModusOperandi propuesta = findById(propuestaId);
        Usuario analista = findAnalista(request.analistaId());

        propuesta.setEstado(EstadoPropuestaMO.APROBADA);
        propuesta.setAnalistaRevisor(analista);
        propuesta.setFechaRevision(LocalDateTime.now());
        propuesta.setRevisadoPorExperto(true);

        return toResponse(propuestaRepository.save(propuesta));
    }

    @Override
    public PropuestaModusOperandiResponse corregir(Long propuestaId, CorregirPropuestaMoRequest request) {
        PropuestaModusOperandi propuesta = findById(propuestaId);
        Usuario analista = findAnalista(request.analistaId());

        if (request.caracteristicasComunes() != null) {
            propuesta.setCaracteristicasComunes(request.caracteristicasComunes());
        }
        if (request.posibleFirma() != null) {
            propuesta.setPosibleFirma(request.posibleFirma());
        }
        if (request.consistenciaHorarioZona() != null) {
            propuesta.setConsistenciaHorarioZona(request.consistenciaHorarioZona());
        }

        propuesta.setEstado(EstadoPropuestaMO.CORREGIDA);
        propuesta.setAnalistaRevisor(analista);
        propuesta.setJustificacionRevision(request.justificacion());
        propuesta.setFechaRevision(LocalDateTime.now());
        propuesta.setRevisadoPorExperto(true);

        return toResponse(propuestaRepository.save(propuesta));
    }

    @Override
    public PropuestaModusOperandiResponse rechazar(Long propuestaId, RechazarPropuestaMoRequest request) {
        PropuestaModusOperandi propuesta = findById(propuestaId);
        Usuario analista = findAnalista(request.analistaId());

        propuesta.setEstado(EstadoPropuestaMO.RECHAZADA);
        propuesta.setClasificacionManual(request.clasificacionManual());
        propuesta.setAnalistaRevisor(analista);
        propuesta.setJustificacionRevision(request.justificacion());
        propuesta.setFechaRevision(LocalDateTime.now());
        propuesta.setRevisadoPorExperto(true);

        return toResponse(propuestaRepository.save(propuesta));
    }

    private PropuestaModusOperandi findById(Long propuestaId) {
        return propuestaRepository.findById(propuestaId)
                .orElseThrow(() -> new ResourceNotFoundException("PropuestaModusOperandi", propuestaId));
    }

    private Usuario findAnalista(UUID analistaId) {
        return usuarioRepository.findById(analistaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", analistaId));
    }

    private PropuestaModusOperandiResponse toResponse(PropuestaModusOperandi p) {
        if (p.getExpediente() == null) {
            throw new BusinessException("La propuesta " + p.getId() + " no tiene expediente asociado.");
        }

        List<ExpedienteSimilarResponse> similares = p.getExpedientesSimilares() == null ? List.of() :
                p.getExpedientesSimilares().stream()
                .map(s -> new ExpedienteSimilarResponse(s.getExpedienteId(), s.getFolio(), s.getSimilitudPorcentaje()))
                .toList();

        return new PropuestaModusOperandiResponse(
                p.getId(),
                p.getExpediente().getId(),
                p.getExpediente().getFolio(),
                p.getVersion(),
                p.isVigente(),
                p.getEstado(),
                p.getCaracteristicasComunes(),
                p.getPosibleFirma(),
                p.getConsistenciaHorarioZona(),
                p.getResumenGenerado(),
                p.getNivelConfianza(),
                p.getModeloEmbedding(),
                p.getModeloChat(),
                p.getFechaGeneracion(),
                similares,
                p.isRevisadoPorExperto(),
                p.getAnalistaRevisor() != null ? p.getAnalistaRevisor().getId() : null,
                p.getAnalistaRevisor() != null ? p.getAnalistaRevisor().getFullName() : null,
                p.getJustificacionRevision(),
                p.getClasificacionManual(),
                p.getFechaRevision());
    }
}