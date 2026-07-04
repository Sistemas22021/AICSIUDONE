package equipoBlanco.com.prison_service.modules.control.service;

import equipoBlanco.com.prison_service.modules.control.dto.AlertaDto;
import equipoBlanco.com.prison_service.modules.control.dto.AtenderAlertaDto;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;

    /**
     * Devuelve las alertas activas de un destinatario específico.
     * Usada por la campana de notificaciones, filtrando por el usuario logueado.
     */
    public List<AlertaDto> obtenerAlertasActivasPorDestinatario(String destinatario) {
        return alertaRepository
                .findByDestinatarioAndEstado(destinatario, "activa")
                .stream()
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
     * Marca la alerta como "atendida" y registra la observación opcional del oficial.
     */
    @Transactional
    public AlertaDto atenderAlerta(UUID id, AtenderAlertaDto dto) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + id));

        alerta.marcarAtendida(dto.getObservacion());
        return toDto(alertaRepository.save(alerta));
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
