package equipoBlanco.com.prison_service.modules.inmates.service;

import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import equipoBlanco.com.prison_service.modules.cells.repository.CellRepository;
import equipoBlanco.com.prison_service.modules.inmates.dto.ConcludeIncidentDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.DeathReportDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.IncidentParticipantDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.InternalIncidentDto;
import equipoBlanco.com.prison_service.modules.inmates.model.*;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.repository.DeathReportRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.IncidentParticipantRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.InternalIncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeathReportService {

    private final DeathReportRepository deathReportRepository;
    private final InternalIncidentRepository internalIncidentRepository;
    private final IncidentParticipantRepository incidentParticipantRepository;
    private final InmateRepository inmateRepository;
    private final CellRepository cellRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public DeathReportDto registerNaturalDeath(UUID inmateId, DeathReportDto dto, String username) {
        Inmate inmate = inmateRepository.findById(inmateId)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado"));

        if (inmate.getStatus() == InmateStatus.EGRESADO) {
            throw new RuntimeException("El recluso ya se encuentra egresado");
        }

        LocalDateTime eventTime = dto.getDateTimeFound() != null ? dto.getDateTimeFound() : LocalDateTime.now();

        DeathReport report = DeathReport.builder()
            .inmate(inmate)
            .deceaseType("NATURAL")
            .dateTimeFound(eventTime)
            .description(dto.getDescription())
            .build();
        deathReportRepository.save(report);

        // ── Registrar en bitácora de incidentes (ya cerrado, no genera investigación) ──
        String code = String.format("INC-%d-%04d", eventTime.getYear(), internalIncidentRepository.count() + 1);
        String inmateName = inmate.getFirstName() + " " + inmate.getFirstLastname();
        InternalIncident naturalIncident = InternalIncident.builder()
            .code(code)
            .cellId(inmate.getCell() != null ? inmate.getCell().getId() : null)
            .cellIdentifier(inmate.getCell() != null ? inmate.getCell().getIdentifier() : "N/A")
            .description("Fallecimiento Natural. " + dto.getDescription())
            .incidentDate(eventTime)
            .reporter(username)
            .status("CERRADO")
            .conclusionType("NATURAL")
            .causaMedicaDefinitiva(dto.getDescription())
            .concludedAt(eventTime)
            .concludedBy(username)
            .responsableNoAplica(true)
            .build();
        InternalIncident savedNaturalIncident = internalIncidentRepository.save(naturalIncident);

        // Participante fallecido en el incidente natural
        IncidentParticipant part = IncidentParticipant.builder()
            .incident(savedNaturalIncident)
            .inmate(inmate)
            .role("FALLECIDO")
            .initialStatus("FALLECIDO")
            .comments("Fallecimiento Natural. " + dto.getDescription())
            .build();
        incidentParticipantRepository.save(part);

        // ── Finalizar egreso ──────────────────────────────────────────────────
        inmate.setStatus(InmateStatus.EGRESADO);
        inmate.setCell(null);
        inmate.setDischargeDate(eventTime.toLocalDate());
        inmate.setMotivoEgreso("Fallecimiento");
        inmate.setObservacionesEgreso("Fallecimiento Natural: " + dto.getDescription());

        if (inmate.getStatusHistory() == null) {
            inmate.setStatusHistory(new ArrayList<>());
        }
        inmate.getStatusHistory().add(String.format(
            "[%s] Fallecimiento Natural registrado por %s. Acta de incidente [%s] generada. Expediente cerrado.",
            LocalDateTime.now().format(FORMATTER), username, code));
        inmateRepository.save(inmate);

        return toDto(report);
    }

    @Transactional
    public DeathReportDto registerNonNaturalDeathDraft(UUID inmateId, DeathReportDto dto) {
        Inmate inmate = inmateRepository.findById(inmateId)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado"));

        if (inmate.getStatus() == InmateStatus.EGRESADO) {
            throw new RuntimeException("El recluso ya se encuentra egresado");
        }

        // Check if draft already exists
        DeathReport report = deathReportRepository.findByInmateId(inmateId)
            .orElse(null);

        if (report == null) {
            report = DeathReport.builder()
                .inmate(inmate)
                .deceaseType("NO_NATURAL")
                .dateTimeFound(dto.getDateTimeFound() != null ? dto.getDateTimeFound() : LocalDateTime.now())
                .description(dto.getDescription())
                .build();
        } else {
            report.setDeceaseType("NO_NATURAL");
            report.setDateTimeFound(dto.getDateTimeFound() != null ? dto.getDateTimeFound() : LocalDateTime.now());
            report.setDescription(dto.getDescription());
        }
        deathReportRepository.save(report);

        return toDto(report);
    }

    @Transactional
    public InternalIncidentDto createIncidentAndFinalize(InternalIncidentDto dto, String username) {
        Inmate deceased = inmateRepository.findById(dto.getParticipants().stream()
            .filter(p -> "FALLECIDO".equalsIgnoreCase(p.getRole()))
            .map(IncidentParticipantDto::getInmateId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Debe incluir al fallecido en los participantes del incidente")))
            .orElseThrow(() -> new RuntimeException("Recluso fallecido no encontrado"));

        Cell cell = deceased.getCell();
        if (cell == null) {
            throw new RuntimeException("El recluso fallecido no tiene celda asignada para investigar");
        }

        // Generate Code
        String code = String.format("INC-%d-%04d", LocalDateTime.now().getYear(), internalIncidentRepository.count() + 1);

        InternalIncident incident = InternalIncident.builder()
            .code(code)
            .cellId(cell.getId())
            .cellIdentifier(cell.getIdentifier())
            .description(dto.getDescription())
            .incidentDate(dto.getIncidentDate() != null ? dto.getIncidentDate() : LocalDateTime.now())
            .reporter(dto.getReporter() != null ? dto.getReporter() : username)
            .status("EN_INVESTIGACION")
            .build();
        internalIncidentRepository.save(incident);

        // Block Cell
        cell.setBlockedForInvestigation(true);
        cellRepository.save(cell);

        // Close deceased record
        deceased.setStatus(InmateStatus.EGRESADO);
        deceased.setCell(null);
        deceased.setDischargeDate(incident.getIncidentDate().toLocalDate());
        deceased.setMotivoEgreso("Fallecimiento");
        deceased.setObservacionesEgreso("Fallecimiento No Natural. Incidente: " + code);
        if (deceased.getStatusHistory() == null) {
            deceased.setStatusHistory(new ArrayList<>());
        }
        deceased.getStatusHistory().add(String.format("[%s] Fallecimiento No Natural registrado. Expediente de Incidente Interno [Ver Incidente: %s](/incidentes/detalle/%s) levantado. Celda bloqueada.",
            LocalDateTime.now().format(FORMATTER), code, incident.getId()));
        inmateRepository.save(deceased);

        // Save deceased participant relation
        IncidentParticipant decPart = IncidentParticipant.builder()
            .incident(incident)
            .inmate(deceased)
            .role("FALLECIDO")
            .initialStatus("FALLECIDO")
            .comments(dto.getDescription())
            .build();
        incidentParticipantRepository.save(decPart);

        // Process co-habitants (excluding temporary egress)
        List<IncidentParticipantDto> results = new ArrayList<>();
        results.add(toParticipantDto(decPart));

        List<Inmate> cohabitants = inmateRepository.findAll().stream()
            .filter(in -> in.getCell() != null && in.getCell().getId().equals(cell.getId()) && !in.getId().equals(deceased.getId()))
            .filter(in -> in.getStatus() != InmateStatus.ACTIVO_SALIDA_TEMPORAL)
            .toList();

        for (Inmate coh : cohabitants) {
            // Find request participant dto to fetch custom notes/status
            IncidentParticipantDto reqDto = dto.getParticipants().stream()
                .filter(p -> p.getInmateId().equals(coh.getId()))
                .findFirst()
                .orElse(null);

            String initStat = reqDto != null ? reqDto.getInitialStatus() : "ILESO";
            String comms = reqDto != null ? reqDto.getComments() : "Presente en celda.";

            coh.setStatus(InmateStatus.PENDIENTE_REUBICACION_EMERGENCIA);
            if (coh.getStatusHistory() == null) {
                coh.setStatusHistory(new ArrayList<>());
            }
            coh.getStatusHistory().add(String.format("[%s] Involucrado en incidente registrado en Celda %s (Incidente [Ver Incidente: %s](/incidentes/detalle/%s)). Estado inicial: %s. Notas: %s. Pendiente de reubicación.",
                LocalDateTime.now().format(FORMATTER), cell.getIdentifier(), code, incident.getId(), initStat, comms));
            inmateRepository.save(coh);

            IncidentParticipant cohPart = IncidentParticipant.builder()
                .incident(incident)
                .inmate(coh)
                .role("COHABITANTE")
                .initialStatus(initStat)
                .comments(comms)
                .build();
            incidentParticipantRepository.save(cohPart);

            results.add(toParticipantDto(cohPart));
        }

        // Return detailed incident
        return toIncidentDto(incident, results);
    }

    public List<InternalIncidentDto> getAllIncidents() {
        return internalIncidentRepository.findAll().stream()
            .map(inc -> toIncidentDto(inc, incidentParticipantRepository.findByIncidentId(inc.getId()).stream().map(this::toParticipantDto).toList()))
            .toList();
    }

    public InternalIncidentDto getIncidentById(UUID id) {
        InternalIncident inc = internalIncidentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Incidente no encontrado"));
        List<IncidentParticipantDto> parts = incidentParticipantRepository.findByIncidentId(id).stream()
            .map(this::toParticipantDto).toList();
        return toIncidentDto(inc, parts);
    }

    @Transactional
    public InternalIncidentDto concludeIncident(UUID id, ConcludeIncidentDto dto, String username) {
        InternalIncident inc = internalIncidentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Incidente no encontrado"));

        if (!"EN_INVESTIGACION".equals(inc.getStatus())) {
            throw new RuntimeException("El incidente ya está cerrado o en un estado que no permite cierre.");
        }

        // Validar campos legales obligatorios
        if (dto.getAutopsiaProtocolo() == null || dto.getAutopsiaProtocolo().isBlank()) {
            throw new RuntimeException("El Número de Protocolo de Autopsia es obligatorio para cerrar la investigación.");
        }
        if (dto.getFiscaliaExpediente() == null || dto.getFiscaliaExpediente().isBlank()) {
            throw new RuntimeException("El Número de Expediente de la Fiscalía es obligatorio para cerrar la investigación.");
        }

        inc.setConclusionType(dto.getConclusionType());
        inc.setCausaMedicaDefinitiva(dto.getCausaMedicaDefinitiva());
        inc.setAutopsiaProtocolo(dto.getAutopsiaProtocolo());
        inc.setFiscaliaExpediente(dto.getFiscaliaExpediente());
        inc.setResponsableInmateId(dto.getResponsableInmateId());
        inc.setResponsablePersonal(dto.getResponsablePersonal());
        inc.setResponsableNoAplica(Boolean.TRUE.equals(dto.getResponsableNoAplica()));
        inc.setConcludedAt(LocalDateTime.now());
        inc.setConcludedBy(username);
        inc.setStatus("CERRADO");

        // Desbloquear la celda automáticamente al cerrar la investigación
        if (inc.getCellId() != null) {
            cellRepository.findById(inc.getCellId()).ifPresent(cell -> {
                cell.setBlockedForInvestigation(false);
                cellRepository.save(cell);
            });
        }

        inc.setAdditionalSentenceYears(dto.getAdditionalSentenceYears());
        inc.setAdditionalSentenceMonths(dto.getAdditionalSentenceMonths());

        internalIncidentRepository.save(inc);

        // ── Imputar condena adicional al responsable (si aplica) ───────────────────
        if (!Boolean.TRUE.equals(dto.getResponsableNoAplica()) && dto.getResponsableInmateId() != null) {
            Inmate responsable = inmateRepository.findById(dto.getResponsableInmateId())
                .orElseThrow(() -> new RuntimeException("Recluso responsable no encontrado: " + dto.getResponsableInmateId()));

            int addYears = dto.getAdditionalSentenceYears() != null ? dto.getAdditionalSentenceYears() : 0;
            int addMonths = dto.getAdditionalSentenceMonths() != null ? dto.getAdditionalSentenceMonths() : 0;

            if (addYears > 0 || addMonths > 0) {
                int currentYears = responsable.getSentenceYears() != null ? responsable.getSentenceYears() : 0;
                int currentMonths = responsable.getSentenceMonths() != null ? responsable.getSentenceMonths() : 0;

                int totalMonths = (currentYears * 12 + currentMonths) + (addYears * 12 + addMonths);
                responsable.setSentenceYears(totalMonths / 12);
                responsable.setSentenceMonths(totalMonths % 12);

                // Recalcular fecha estimada de egreso
                if (responsable.getAdmissionDate() != null) {
                    responsable.setEstimatedReleaseDate(
                        responsable.getAdmissionDate().plusYears(responsable.getSentenceYears())
                            .plusMonths(responsable.getSentenceMonths())
                    );
                }

                String responsableName = responsable.getFirstName() + " " + responsable.getFirstLastname();
                if (responsable.getStatusHistory() == null) responsable.setStatusHistory(new ArrayList<>());
                responsable.getStatusHistory().add(String.format(
                    "[%s] Condena ampliada en %d año(s) y %d mes(es) por responsabilidad en incidente %s (concluido por %s). Nueva condena total: %d años %d meses.",
                    LocalDateTime.now().format(FORMATTER), addYears, addMonths, inc.getCode(), username,
                    responsable.getSentenceYears(), responsable.getSentenceMonths()));
                inmateRepository.save(responsable);
            }
        }

        List<IncidentParticipantDto> parts = incidentParticipantRepository.findByIncidentId(id).stream()
            .map(this::toParticipantDto).toList();
        return toIncidentDto(inc, parts);
    }

    public DeathReportDto getDeathReportByInmate(UUID inmateId) {
        DeathReport report = deathReportRepository.findByInmateId(inmateId)
            .orElseThrow(() -> new RuntimeException("Reporte de fallecimiento no encontrado"));
        return toDto(report);
    }

    private DeathReportDto toDto(DeathReport r) {
        return DeathReportDto.builder()
            .id(r.getId())
            .inmateId(r.getInmate().getId())
            .deceaseType(r.getDeceaseType())
            .dateTimeFound(r.getDateTimeFound())
            .description(r.getDescription())
            .build();
    }

    private IncidentParticipantDto toParticipantDto(IncidentParticipant p) {
        return IncidentParticipantDto.builder()
            .id(p.getId())
            .inmateId(p.getInmate().getId())
            .inmateName(p.getInmate().getFirstName() + " " + p.getInmate().getFirstLastname())
            .inmateCedula(p.getInmate().getCedula())
            .role(p.getRole())
            .initialStatus(p.getInitialStatus())
            .comments(p.getComments())
            .build();
    }

    private InternalIncidentDto toIncidentDto(InternalIncident inc, List<IncidentParticipantDto> parts) {
        // Resolver nombre del responsable si hay ID
        String responsableInmateName = null;
        if (inc.getResponsableInmateId() != null) {
            responsableInmateName = inmateRepository.findById(inc.getResponsableInmateId())
                .map(r -> r.getFirstName() + " " + r.getFirstLastname())
                .orElse(null);
        }

        return InternalIncidentDto.builder()
            .id(inc.getId())
            .code(inc.getCode())
            .cellId(inc.getCellId())
            .cellIdentifier(inc.getCellIdentifier())
            .description(inc.getDescription())
            .incidentDate(inc.getIncidentDate())
            .reporter(inc.getReporter())
            .status(inc.getStatus())
            .participants(parts)
            .conclusionType(inc.getConclusionType())
            .causaMedicaDefinitiva(inc.getCausaMedicaDefinitiva())
            .autopsiaProtocolo(inc.getAutopsiaProtocolo())
            .fiscaliaExpediente(inc.getFiscaliaExpediente())
            .responsableInmateId(inc.getResponsableInmateId())
            .responsableInmateName(responsableInmateName)
            .responsablePersonal(inc.getResponsablePersonal())
            .responsableNoAplica(inc.getResponsableNoAplica())
            .concludedAt(inc.getConcludedAt())
            .concludedBy(inc.getConcludedBy())
            .additionalSentenceYears(inc.getAdditionalSentenceYears())
            .additionalSentenceMonths(inc.getAdditionalSentenceMonths())
            .build();
    }
}
