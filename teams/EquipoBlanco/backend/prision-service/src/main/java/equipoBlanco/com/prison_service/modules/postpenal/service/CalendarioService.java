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
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    public List<CalendarioDto> obtenerIncumplimientosUltimos30Dias(String oficialCedula) {
        LocalDate hace30Dias = LocalDate.now().minusDays(30);
        List<CalendarioPresentacion> incumplimientos = calendarioRepository.findByEstadoAndFechaProgramadaGreaterThanEqual("INCUMPLIDA", hace30Dias);

        if (oficialCedula != null && !oficialCedula.isEmpty()) {
            return incumplimientos.stream()
                .filter(p -> {
                    ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(p.getExpedienteId()).orElse(null);
                    return exp != null && (oficialCedula.equals(exp.getOficialAsignadoCedula()) || oficialCedula.equals(exp.getOficialAsignadoNombre()));
                })
                .map(this::toDto)
                .collect(Collectors.toList());
        }

        return incumplimientos.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CalendarioDto registrarCumplimiento(UUID id, CumplimientoDto dto) {
        CalendarioPresentacion cp = calendarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentación no encontrada: " + id));

        // Validar que no esté ya cumplida (bloquear reescrituras)
        if ("CUMPLIDA".equals(cp.getEstado())) {
            throw new RuntimeException("Esta presentación ya fue registrada como cumplida y no puede modificarse.");
        }

        cp.setEstado("CUMPLIDA");
        cp.setFechaReal(LocalDate.now());

        // Parsear hora real
        if (dto.getHoraReal() != null && !dto.getHoraReal().isEmpty()) {
            try {
                cp.setHoraReal(LocalTime.parse(dto.getHoraReal(), DateTimeFormatter.ofPattern("HH:mm")));
            } catch (Exception e) {
                cp.setHoraReal(LocalTime.now());
            }
        } else {
            cp.setHoraReal(LocalTime.now());
        }

        String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
        cp.setObservaciones(obsActual + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + dto.getOficialQueRegistro() + "]: Cumplida - " + (dto.getObservaciones() != null ? dto.getObservaciones() : "Sin observaciones"));

        CalendarioPresentacion saved = calendarioRepository.save(cp);

        // Registrar en statusHistory del recluso
        try {
            ExpedienteSeguimiento exp = expedienteSeguimientoRepository.findById(cp.getExpedienteId()).orElse(null);
            if (exp != null) {
                Inmate inmate = inmateRepository.findById(exp.getIdRecluso()).orElse(null);
                if (inmate != null) {
                    if (inmate.getStatusHistory() == null) {
                        inmate.setStatusHistory(new ArrayList<>());
                    }
                    inmate.getStatusHistory().add(String.format("[%s] Presentación del %s cumplida. Hora: %s. Registrado por: %s. Obs: %s",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        cp.getFechaProgramada(),
                        cp.getHoraReal(),
                        dto.getOficialQueRegistro(),
                        dto.getObservaciones() != null ? dto.getObservaciones() : "Ninguna"));
                    inmateRepository.save(inmate);
                }
            }
        } catch (Exception e) {
            System.err.println("Error registrando cumplimiento en historial del recluso: " + e.getMessage());
        }

        return toDto(saved);
    }

    @Transactional
    public CalendarioDto registrarIncumplimiento(UUID id, IncumplimientoDto dto) {
        CalendarioPresentacion cp = calendarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentación no encontrada: " + id));

        // Validar que no haya sido ya procesada como INCUMPLIDA
        if ("INCUMPLIDA".equals(cp.getEstado())) {
            throw new RuntimeException("Esta presentación ya fue registrada como incumplida. No se puede duplicar el registro.");
        }

        cp.setEstado("INCUMPLIDA");
        cp.setDetectadoPorSistema(false);
        cp.setFechaIncumplimiento(dto.getFechaDetectada() != null ? dto.getFechaDetectada() : LocalDateTime.now());

        String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
        cp.setObservaciones(obsActual + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + dto.getOficialQueRegistro() + "]: Incumplida (registrado por oficial) - " + (dto.getObservaciones() != null ? dto.getObservaciones() : "Sin observaciones"));

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

        // Registrar en statusHistory del recluso
        try {
            Inmate inmate = inmateRepository.findById(exp.getIdRecluso()).orElse(null);
            if (inmate != null) {
                if (inmate.getStatusHistory() == null) {
                    inmate.setStatusHistory(new ArrayList<>());
                }
                inmate.getStatusHistory().add(String.format("[%s] Incumplimiento #%d registrado por oficial %s. Obs: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    contador,
                    dto.getOficialQueRegistro(),
                    dto.getObservaciones() != null ? dto.getObservaciones() : "Ninguna"));
                inmateRepository.save(inmate);
            }
        } catch (Exception e) {
            System.err.println("Error registrando incumplimiento en historial del recluso: " + e.getMessage());
        }

        return toDto(cp);
    }

    @Transactional
    public void procesarPresentacionesVencidas() {
        LocalDate hoy = LocalDate.now();
        List<CalendarioPresentacion> vencidas = calendarioRepository.findByFechaProgramadaLessThanEqualAndEstado(hoy, "PENDIENTE");

        for (CalendarioPresentacion cp : vencidas) {
            cp.setEstado("INCUMPLIDA");
            cp.setDetectadoPorSistema(true);
            cp.setFechaIncumplimiento(LocalDateTime.now());

            String obsActual = cp.getObservaciones() == null ? "" : cp.getObservaciones() + "\n";
            cp.setObservaciones(obsActual + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - Sistema]: Incumplida - Detectado por sistema");
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
        // Resolver nombre y cédula del egresado para enriquecer la alerta
        String nombreEgresado = null;
        String cedulaEgresado = null;
        try {
            var inmate = inmateRepository.findById(exp.getIdRecluso()).orElse(null);
            if (inmate != null) {
                nombreEgresado = inmate.getFirstName() + " " + inmate.getFirstLastname();
                cedulaEgresado = inmate.getCedula();
            }
        } catch (Exception e) {
            System.err.println("No se pudo resolver datos del egresado para alerta: " + e.getMessage());
        }

        String mensaje = "Incumplimiento #" + contador + " registrado para: "
                + (nombreEgresado != null ? nombreEgresado : "Expediente " + exp.getId());

        if (contador == 1) {
            Alerta alerta = Alerta.builder()
                .nivel(1)
                .expedienteId(exp.getId())
                .nombreEgresado(nombreEgresado)
                .cedulaEgresado(cedulaEgresado)
                .fechaEmision(LocalDateTime.now())
                .destinatario(exp.getOficialAsignadoNombre())
                .estado("activa")
                .accionRequerida(mensaje)
                .build();
            alertaRepository.save(alerta);
        } else if (contador == 2) {
            Alerta alerta1 = Alerta.builder()
                .nivel(2)
                .expedienteId(exp.getId())
                .nombreEgresado(nombreEgresado)
                .cedulaEgresado(cedulaEgresado)
                .fechaEmision(LocalDateTime.now())
                .destinatario(exp.getOficialAsignadoNombre())
                .estado("activa")
                .accionRequerida(mensaje)
                .build();
            Alerta alerta2 = Alerta.builder()
                .nivel(2)
                .expedienteId(exp.getId())
                .nombreEgresado(nombreEgresado)
                .cedulaEgresado(cedulaEgresado)
                .fechaEmision(LocalDateTime.now())
                .destinatario("Supervisor")
                .estado("activa")
                .accionRequerida(mensaje)
                .build();
            alertaRepository.saveAll(List.of(alerta1, alerta2));
        } else if (contador >= 3) {
            Alerta alerta = Alerta.builder()
                .nivel(3)
                .expedienteId(exp.getId())
                .nombreEgresado(nombreEgresado)
                .cedulaEgresado(cedulaEgresado)
                .fechaEmision(LocalDateTime.now())
                .destinatario("Supervisor")
                .estado("activa")
                .accionRequerida("CRÍTICO: Incumplimiento #" + contador + " - Solicitud de medida urgente ante tribunal")
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
            .fechaReal(c.getFechaReal())
            .horaReal(c.getHoraReal())
            .detectadoPorSistema(c.getDetectadoPorSistema())
            .fechaIncumplimiento(c.getFechaIncumplimiento())
            .build();
    }
}
