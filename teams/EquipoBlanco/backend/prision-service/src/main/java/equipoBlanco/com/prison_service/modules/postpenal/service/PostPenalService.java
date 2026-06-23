package equipoBlanco.com.prison_service.modules.postpenal.service;

import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import equipoBlanco.com.prison_service.modules.postpenal.repository.ExpedienteSeguimientoRepository;
import equipoBlanco.com.prison_service.modules.postpenal.dto.AssignOfficerDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpProfileDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpedienteDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.OficialCargaDto;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostPenalService {

    private final ExpedienteSeguimientoRepository expedienteSeguimientoRepository;
    private final AlertaRepository alertaRepository;
    private final InmateRepository inmateRepository;

    // Lista estática de oficiales de seguimiento disponibles en el sistema
    private static final List<OficialCargaDto> OFICIALES_BASE = List.of(
        new OficialCargaDto("María González", "V-15234567", 0),
        new OficialCargaDto("Juan Pérez", "V-18345678", 0),
        new OficialCargaDto("Ana Rodríguez", "V-20456789", 0),
        new OficialCargaDto("Carlos López", "V-17567890", 0),
        new OficialCargaDto("Laura Martínez", "V-19678901", 0)
    );

    public void createBaseProfile(Inmate inmate, LocalDate fechaEgreso) {
        ExpedienteSeguimiento expediente = ExpedienteSeguimiento.builder()
            .idRecluso(inmate.getId())
            .fechaEgreso(fechaEgreso)
            .estado("pendiente")
            .historialAsignaciones(new ArrayList<>())
            .build();

        expedienteSeguimientoRepository.save(expediente);

        Alerta alerta = Alerta.builder()
            .nivel(2)
            .fechaEmision(LocalDateTime.now())
            .destinatario("Supervisor")
            .estado("activa")
            .accionRequerida("Asignar oficial de seguimiento al egresado (Cédula: " + inmate.getCedula() + ")")
            .build();

        alertaRepository.save(alerta);
    }

    /**
     * Devuelve los expedientes sin oficial asignado, ordenados por fecha de egreso (más antiguos primero).
     */
    public List<ExpedienteDto> getUnassignedExpedientes() {
        List<ExpedienteSeguimiento> expedientes = expedienteSeguimientoRepository
            .findByEstadoOrderByFechaEgresoAsc("pendiente");

        return expedientes.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Devuelve todos los expedientes (asignados y pendientes).
     */
    public List<ExpedienteDto> getAllExpedientes() {
        List<ExpedienteSeguimiento> expedientes = expedienteSeguimientoRepository.findAll();
        expedientes.sort(Comparator.comparing(
            (ExpedienteSeguimiento e) -> "pendiente".equals(e.getEstado()) ? 0 : 1)
            .thenComparing(e -> e.getFechaEgreso() != null ? e.getFechaEgreso() : LocalDate.MAX));

        return expedientes.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Devuelve la lista de oficiales con su carga de casos activos calculada dinámicamente.
     */
    public List<OficialCargaDto> getOficialesConCarga() {
        List<Object[]> cargaData = expedienteSeguimientoRepository.countCasosActivosByOficial();

        // Crear mapa de cédula -> carga
        Map<String, Long> cargaMap = new HashMap<>();
        for (Object[] row : cargaData) {
            String cedula = (String) row[0];
            Long count = (Long) row[2];
            cargaMap.put(cedula, count);
        }

        // Devolver todos los oficiales base con su carga real
        return OFICIALES_BASE.stream()
            .map(o -> new OficialCargaDto(
                o.getNombre(),
                o.getCedula(),
                cargaMap.getOrDefault(o.getCedula(), 0L)
            ))
            .sorted(Comparator.comparingLong(OficialCargaDto::getCasosActivos))
            .collect(Collectors.toList());
    }

    /**
     * Asigna (o reasigna) un oficial a un expediente.
     */
    @Transactional
    public ExpedienteDto assignOfficer(UUID expedienteId, AssignOfficerDto dto) {
        ExpedienteSeguimiento expediente = expedienteSeguimientoRepository.findById(expedienteId)
            .orElseThrow(() -> new RuntimeException("Expediente no encontrado con ID: " + expedienteId));

        // Validar motivo obligatorio en reasignación
        boolean isReassignment = expediente.getOficialAsignadoCedula() != null;
        if (isReassignment && (dto.getMotivoCambio() == null || dto.getMotivoCambio().isBlank())) {
            throw new RuntimeException("El motivo de cambio es obligatorio al reasignar un oficial.");
        }

        // Registrar en historial
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String registro;
        if (isReassignment) {
            registro = String.format("[%s] Reasignado de %s (%s) a %s (%s). Motivo: %s",
                timestamp,
                expediente.getOficialAsignadoNombre(), expediente.getOficialAsignadoCedula(),
                dto.getOficialNombre(), dto.getOficialCedula(),
                dto.getMotivoCambio());
        } else {
            registro = String.format("[%s] Asignado a %s (%s)",
                timestamp, dto.getOficialNombre(), dto.getOficialCedula());
        }

        if (expediente.getHistorialAsignaciones() == null) {
            expediente.setHistorialAsignaciones(new ArrayList<>());
        }
        expediente.getHistorialAsignaciones().add(registro);

        // Actualizar expediente
        expediente.setOficialAsignadoNombre(dto.getOficialNombre());
        expediente.setOficialAsignadoCedula(dto.getOficialCedula());
        expediente.setEstado("asignado");

        expedienteSeguimientoRepository.save(expediente);

        // Crear alerta/notificación para el oficial asignado
        Inmate inmate = inmateRepository.findById(expediente.getIdRecluso()).orElse(null);
        String datosRecluso = inmate != null
            ? inmate.getFirstName() + " " + inmate.getFirstLastname() + " (C.I. " + inmate.getCedula() + ")"
            : "Recluso ID: " + expediente.getIdRecluso();

        Alerta alerta = Alerta.builder()
            .nivel(1)
            .fechaEmision(LocalDateTime.now())
            .destinatario(dto.getOficialNombre())
            .estado("activa")
            .accionRequerida("Se le ha asignado el expediente de seguimiento del egresado: " + datosRecluso)
            .build();

        alertaRepository.save(alerta);

        return toDto(expediente);
    }

    /**
     * Completa el perfil de seguimiento de un egresado.
     */
    @Transactional
    public ExpedienteDto completeProfile(UUID id, ExpProfileDto dto) {
        ExpedienteSeguimiento expediente = expedienteSeguimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expediente no encontrado con ID: " + id));

        expediente.setDomicilio(dto.getDomicilio());
        expediente.setMunicipio(dto.getMunicipio());
        expediente.setContactoEmergenciaNombre(dto.getContactoEmergenciaNombre());
        expediente.setContactoEmergenciaTelefono(dto.getContactoEmergenciaTelefono());
        expediente.setNivelRiesgo(dto.getNivelRiesgo());
        expediente.completarPerfil(); // Cambia el estado a completado

        expedienteSeguimientoRepository.save(expediente);

        return toDto(expediente);
    }

    public ExpedienteDto getByInmateId(UUID inmateId) {
        ExpedienteSeguimiento expediente = expedienteSeguimientoRepository.findByIdRecluso(inmateId)
            .orElseThrow(() -> new RuntimeException("Expediente no encontrado para el recluso: " + inmateId));
        return toDto(expediente);
    }

    private ExpedienteDto toDto(ExpedienteSeguimiento e) {
        Inmate inmate = inmateRepository.findById(e.getIdRecluso()).orElse(null);
        return ExpedienteDto.builder()
            .id(e.getId())
            .idRecluso(e.getIdRecluso())
            .reclusoNombre(inmate != null ? inmate.getFirstName() + " " + inmate.getFirstLastname() : "N/A")
            .reclusoCedula(inmate != null ? inmate.getCedula() : "N/A")
            .fechaEgreso(e.getFechaEgreso())
            .oficialAsignadoNombre(e.getOficialAsignadoNombre())
            .oficialAsignadoCedula(e.getOficialAsignadoCedula())
            .estado(e.getEstado())
            .historialAsignaciones(e.getHistorialAsignaciones() != null ? e.getHistorialAsignaciones() : List.of())
            .domicilio(e.getDomicilio())
            .municipio(e.getMunicipio())
            .contactoEmergenciaNombre(e.getContactoEmergenciaNombre())
            .contactoEmergenciaTelefono(e.getContactoEmergenciaTelefono())
            .nivelRiesgo(e.getNivelRiesgo())
            .contadorIncumplimientos(e.getContadorIncumplimientos() != null ? e.getContadorIncumplimientos() : 0)
            .build();
    }
}
