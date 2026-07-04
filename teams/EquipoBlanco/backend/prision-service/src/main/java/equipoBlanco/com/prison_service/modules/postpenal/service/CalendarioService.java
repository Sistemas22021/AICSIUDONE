package equipoBlanco.com.prison_service.modules.postpenal.service;

import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioCreateDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioUpdateDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CumplimientoDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.IncumplimientoDto;
import equipoBlanco.com.prison_service.modules.postpenal.model.CalendarioPresentacion;
import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import equipoBlanco.com.prison_service.modules.postpenal.repository.CalendarioPresentacionRepository;
import equipoBlanco.com.prison_service.modules.postpenal.repository.ExpedienteSeguimientoRepository;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarioService {

    private final CalendarioPresentacionRepository calendarioRepository;
    private final ExpedienteSeguimientoRepository expedienteSeguimientoRepository;
    private final AlertaRepository alertaRepository;
    private final equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository inmateRepository;

    @Transactional
    public List<CalendarioDto> generarCalendario(UUID expedienteId, CalendarioCreateDto dto) {
        if (!expedienteSeguimientoRepository.existsById(expedienteId)) {
            throw new RuntimeException("Expediente no encontrado: " + expedienteId);
        }

        List<CalendarioPresentacion> nuevasPresentaciones = new ArrayList<>();
        LocalDate fechaActual = dto.getFechaInicio();

        // Generar 12 presentaciones futuras por defecto
        int numeroPresentaciones = 12;
        for (int i = 0; i < numeroPresentaciones; i++) {
            CalendarioPresentacion cp = CalendarioPresentacion.builder()
                .expedienteId(expedienteId)
                .fechaProgramada(fechaActual)
                .estado("PENDIENTE")
                .frecuencia(dto.getFrecuencia())
                .fechaInicio(dto.getFechaInicio())
                .oficialQueRegistro(dto.getOficialQueRegistro())
                .build();
            nuevasPresentaciones.add(cp);

            // Calcular siguiente fecha
            switch (dto.getFrecuencia().toUpperCase()) {
                case "SEMANAL":
                    fechaActual = fechaActual.plusWeeks(1);
                    break;
                case "QUINCENAL":
                    fechaActual = fechaActual.plusWeeks(2);
                    break;
                case "MENSUAL":
                    fechaActual = fechaActual.plusMonths(1);
                    break;
                default:
                    throw new IllegalArgumentException("Frecuencia no válida: " + dto.getFrecuencia());
            }
        }

        List<CalendarioPresentacion> saved = calendarioRepository.saveAll(nuevasPresentaciones);
        return saved.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CalendarioDto> obtenerCalendarioPorExpediente(UUID expedienteId) {
        return calendarioRepository.findByExpedienteIdOrderByFechaProgramadaAsc(expedienteId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CalendarioDto actualizarFecha(UUID id, CalendarioUpdateDto dto) {
        CalendarioPresentacion cp = calendarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentación no encontrada: " + id));

        cp.setFechaProgramada(dto.getNuevaFecha());
        if (dto.getObservaciones() != null) {
            String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
            cp.setObservaciones(obsActual + "[" + LocalDate.now() + " - " + dto.getOficialQueRegistro() + "]: Reprogramada a " + dto.getNuevaFecha() + " - " + dto.getObservaciones());
        }

        return toDto(calendarioRepository.save(cp));
    }

    public List<CalendarioDto> obtenerPendientesHoy(String oficialCedula) {
        // En una app real, cruzaríamos con ExpedienteSeguimiento para filtrar por el oficial
        // Para simplicidad en este MVP, devolveremos todas las pendientes de hoy
        LocalDate hoy = LocalDate.now();
        List<CalendarioPresentacion> pendientes = calendarioRepository.findByFechaProgramadaAndEstado(hoy, "PENDIENTE");

        if (oficialCedula != null && !oficialCedula.isEmpty()) {
            return pendientes.stream()
                .filter(p -> {
                    ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(p.getExpedienteId()).orElse(null);
                    return exp != null && (oficialCedula.equals(exp.getOficialAsignadoCedula()) || oficialCedula.equals(exp.getOficialAsignadoNombre()));
                })
                .map(this::toDto)
                .collect(Collectors.toList());
        }

        return pendientes.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CalendarioDto registrarCumplimiento(UUID id, CumplimientoDto dto) {
        CalendarioPresentacion cp = calendarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentación no encontrada: " + id));

        cp.setEstado("CUMPLIDA");
        String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
        cp.setObservaciones(obsActual + "[" + java.time.LocalDateTime.now() + " - " + dto.getOficialQueRegistro() + "]: Cumplida - " + dto.getObservaciones());

        return toDto(calendarioRepository.save(cp));
    }

    @Transactional
    public CalendarioDto registrarIncumplimiento(UUID id, IncumplimientoDto dto) {
        CalendarioPresentacion cp = calendarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentación no encontrada: " + id));

        cp.setEstado("INCUMPLIDA");
        String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
        cp.setObservaciones(obsActual + "[" + java.time.LocalDateTime.now() + " - " + dto.getOficialQueRegistro() + "]: Incumplida - " + dto.getObservaciones());

        calendarioRepository.save(cp);

        // Incrementar contador y generar alerta escalonada
        ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(cp.getExpedienteId())
            .orElseThrow(() -> new RuntimeException("Expediente no encontrado: " + cp.getExpedienteId()));

        int contador = exp.getContadorIncumplimientos() == null ? 0 : exp.getContadorIncumplimientos();
        contador++;
        exp.setContadorIncumplimientos(contador);

        if (contador >= 3) {
            exp.setEstado("alerta_critica");
        }
        expedienteSeguimientoRepository.save(exp);

        crearAlertaEscalonada(exp, contador);

        return toDto(cp);
    }

    @Transactional
    public void procesarPresentacionesVencidas() {
        LocalDate hoy = LocalDate.now();
        List<CalendarioPresentacion> vencidas = calendarioRepository.findByFechaProgramadaLessThanEqualAndEstado(hoy, "PENDIENTE");

        for (CalendarioPresentacion cp : vencidas) {
            cp.setEstado("INCUMPLIDA");
            String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
            cp.setObservaciones(obsActual + "[" + java.time.LocalDateTime.now() + " - Sistema]: Incumplida - Detectado por sistema");
            calendarioRepository.save(cp);

            ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(cp.getExpedienteId())
                .orElseThrow(() -> new RuntimeException("Expediente no encontrado: " + cp.getExpedienteId()));

            int contador = exp.getContadorIncumplimientos() == null ? 0 : exp.getContadorIncumplimientos();
            contador++;
            exp.setContadorIncumplimientos(contador);

            if (contador >= 3) {
                exp.setEstado("alerta_critica");
            }
            expedienteSeguimientoRepository.save(exp);

            crearAlertaEscalonada(exp, contador);
        }
    }

    private void crearAlertaEscalonada(ExpedienteSeguimiento exp, int contador) {
        String mensaje = "Incumplimiento #" + contador + " registrado para expediente ID: " + exp.getIdRecluso();
        
        if (contador == 1) {
            Alerta alerta = Alerta.builder()
                .nivel(1)
                .fechaEmision(java.time.LocalDateTime.now())
                .destinatario(exp.getOficialAsignadoNombre())
                .estado("activa")
                .accionRequerida(mensaje)
                .build();
            alertaRepository.save(alerta);
        } else if (contador == 2) {
            Alerta alerta1 = Alerta.builder()
                .nivel(2)
                .fechaEmision(java.time.LocalDateTime.now())
                .destinatario(exp.getOficialAsignadoNombre())
                .estado("activa")
                .accionRequerida(mensaje)
                .build();
            Alerta alerta2 = Alerta.builder()
                .nivel(2)
                .fechaEmision(java.time.LocalDateTime.now())
                .destinatario("Supervisor")
                .estado("activa")
                .accionRequerida(mensaje)
                .build();
            alertaRepository.saveAll(List.of(alerta1, alerta2));
        } else if (contador >= 3) {
            Alerta alerta = Alerta.builder()
                .nivel(3)
                .fechaEmision(java.time.LocalDateTime.now())
                .destinatario("Supervisor")
                .estado("activa")
                .accionRequerida("CRÍTICO: " + mensaje)
                .build();
            alertaRepository.save(alerta);
        }
    }

    private CalendarioDto toDto(CalendarioPresentacion c) {
        String reclusoNombre = null;
        String reclusoCedula = null;
        try {
            ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(c.getExpedienteId()).orElse(null);
            if (exp != null) {
                var inmate = inmateRepository.findById(exp.getIdRecluso()).orElse(null);
                if (inmate != null) {
                    reclusoNombre = inmate.getFirstName() + " " + inmate.getFirstLastname();
                    reclusoCedula = inmate.getCedula();
                }
            }
        } catch (Exception e) {
            System.err.println("Error enriqueciendo CalendarioDto: " + e.getMessage());
        }

        return CalendarioDto.builder()
            .id(c.getId())
            .expedienteId(c.getExpedienteId())
            .fechaProgramada(c.getFechaProgramada())
            .estado(c.getEstado())
            .frecuencia(c.getFrecuencia())
            .fechaInicio(c.getFechaInicio())
            .oficialQueRegistro(c.getOficialQueRegistro())
            .observaciones(c.getObservaciones())
            .reclusoNombre(reclusoNombre)
            .reclusoCedula(reclusoCedula)
            .build();
    }
}
