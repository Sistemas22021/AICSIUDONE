package equipoBlanco.com.prison_service.modules.control.service;

import equipoBlanco.com.prison_service.modules.control.dto.AlertaDto;
import equipoBlanco.com.prison_service.modules.control.dto.AtenderAlertaDto;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import equipoBlanco.com.prison_service.modules.postpenal.repository.ExpedienteSeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final ExpedienteSeguimientoRepository expedienteSeguimientoRepository;

    /**
     * Devuelve las alertas activas de un destinatario específico.
     * Usada por la campana de notificaciones, filtrando por el usuario logueado.
     * Si el destinatario tiene el rol de Supervisor, también obtiene las alertas
     * dirigidas al destinatario genérico "Supervisor".
     */
    public List<AlertaDto> obtenerAlertasActivasPorDestinatario(String destinatario) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isSupervisor = false;
        if (authentication != null) {
            isSupervisor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERVISOR"));
        }

        List<Alerta> result;
        if (isSupervisor) {
            List<Alerta> userAlerts = alertaRepository.findByDestinatarioAndEstado(destinatario, "activa");
            List<Alerta> supervisorAlerts = alertaRepository.findByDestinatarioAndEstado("Supervisor", "activa");
            List<Alerta> combined = new ArrayList<>(userAlerts);
            for (Alerta a : supervisorAlerts) {
                if (combined.stream().noneMatch(x -> x.getId().equals(a.getId()))) {
                    combined.add(a);
                }
            }
            result = combined;
        } else {
            result = alertaRepository.findByDestinatarioAndEstado(destinatario, "activa");
        }

        return result.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las alertas de Nivel 1 (activas e históricas).
     * Usada por la sección de Alertas N1 en el Dashboard de Control.
     */
    public List<AlertaDto> obtenerAlertasNivel1() {
        return alertaRepository
                .findByNivel(1)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve solo las alertas de Nivel 1 que siguen activas.
     */
    public List<AlertaDto> obtenerAlertasNivel1Activas() {
        return alertaRepository
                .findByNivelAndEstado(1, "activa")
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las alertas de Nivel 2 (activas e históricas).
     * Usada por la sección de Alertas N2 en el Dashboard de Control.
     */
    public List<AlertaDto> obtenerAlertasNivel2() {
        return alertaRepository
                .findByNivel(2)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve solo las alertas de Nivel 2 que siguen activas.
     */
    public List<AlertaDto> obtenerAlertasNivel2Activas() {
        return alertaRepository
                .findByNivelAndEstado(2, "activa")
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las alertas de Nivel 3 (activas e históricas).
     */
    public List<AlertaDto> obtenerAlertasNivel3() {
        return alertaRepository
                .findByNivel(3)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Marca la alerta como "atendida" y registra la observación opcional del oficial.
     */
    @Transactional
    public AlertaDto atenderAlerta(UUID id, AtenderAlertaDto dto) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + id));

        alerta.marcarAtendida(dto.getObservacion());
        AlertaDto result = toDto(alertaRepository.save(alerta));

        // Si es una alerta de nivel 2 y hay otra alerta hermana activa para el mismo expediente, atenderla también
        if (alerta.getNivel() == 2 && alerta.getExpedienteId() != null) {
            List<Alerta> hermanas = alertaRepository.findByExpedienteIdAndNivelAndEstado(alerta.getExpedienteId(), 2, "activa");
            for (Alerta hermana : hermanas) {
                if (!hermana.getId().equals(alerta.getId())) {
                    hermana.marcarAtendida(dto.getObservacion() + " (Atendida por otro rol vinculado)");
                    alertaRepository.save(hermana);
                }
            }
        }
        
        // Si es una alerta de nivel 3, cambiar el estado del expediente
        if (alerta.getNivel() == 3 && alerta.getExpedienteId() != null) {
            ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(alerta.getExpedienteId())
                    .orElse(null);
            if (exp != null) {
                exp.setEstado("Alerta Crítica — Atendida");
                expedienteSeguimientoRepository.save(exp);
            }
        }

        return result;
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────

    private AlertaDto toDto(Alerta a) {
        return AlertaDto.builder()
                .id(a.getId())
                .nivel(a.getNivel())
                .fechaEmision(a.getFechaEmision())
                .destinatario(a.getDestinatario())
                .estado(a.getEstado())
                .accionRequerida(a.getAccionRequerida())
                .expedienteId(a.getExpedienteId())
                .nombreEgresado(a.getNombreEgresado())
                .cedulaEgresado(a.getCedulaEgresado())
                .observacionAtencion(a.getObservacionAtencion())
                .build();
    }
}
