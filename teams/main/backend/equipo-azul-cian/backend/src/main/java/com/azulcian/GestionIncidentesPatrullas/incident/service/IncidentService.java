package com.azulcian.GestionIncidentesPatrullas.incident.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentDetailDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentSummaryDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.repository.IncidentRepository;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);

    private final IncidentRepository incidentRepository;
    private final AssignmentRepository assignmentRepository;
    private final PatrolRepository patrolRepository;

    public IncidentService(
            IncidentRepository incidentRepository,
            AssignmentRepository assignmentRepository,
            PatrolRepository patrolRepository
    ) {
        this.incidentRepository = incidentRepository;
        this.assignmentRepository = assignmentRepository;
        this.patrolRepository = patrolRepository;
    }

    // =========================================
    // CREATE INCIDENT
    // =========================================
    public Incident createIncident(Incident incident) {
        logger.debug(
                "Creando incidente de tipo {}",
                incident.getType()
        );

        incident.setStatus(IncidentStatus.ACTIVE);
        Incident saved = incidentRepository.save(incident);

        logger.info(
                "Nuevo incidente registrado. ID={}, Tipo={}, Prioridad={}",
                saved.getId(),
                saved.getType(),
                saved.getPriority()
        );

        return saved;
    }

    // =========================================
    // LIST RECENT INCIDENTS
    // =========================================
    public List<IncidentDetailDTO> getAllIncidents() {
        return incidentRepository
                .findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDetailDTO)
                .toList();
    }

    // =========================================
    // GET BY ID (INTERNAL / ENTITY)
    // =========================================
    public Incident getIncidentById(Long id) {
        logger.debug("Buscando incidente con ID={}", id);

        return incidentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(
                            "No se encontró el incidente con ID={}",
                            id
                    );
                    return new RuntimeException("Incident not found with id: " + id);
                });
    }

    // =========================================
    // GET DETAILS BY ID (DTO FOR VIEW)
    // =========================================
    public IncidentDetailDTO getIncidentDetailById(Long id) {
        Incident incident = getIncidentById(id);
        return convertToDetailDTO(incident);
    }

    // =========================================
    // METODO AUXILIAR: CONVERT TO DETAIL DTO
    // =========================================
    private IncidentDetailDTO convertToDetailDTO(Incident incident) {
        IncidentDetailDTO dto = new IncidentDetailDTO();

        dto.setId(incident.getId());
        dto.setType(incident.getType());
        dto.setDescription(incident.getDescription());
        dto.setLatitude(incident.getLatitude());
        dto.setLongitude(incident.getLongitude());
        dto.setStatus(incident.getStatus());
        dto.setPriority(incident.getPriority());
        dto.setCreatedAt(incident.getCreatedAt());
        dto.setUpdatedAt(incident.getUpdatedAt());
        dto.setClosedAt(incident.getClosedAt());

        assignmentRepository
                .findByIncident(incident)
                .ifPresent(assignment -> {
                    Patrol patrol = assignment.getPatrol();
                    dto.setPatrolCode(patrol.getCode());
                    dto.setPatrolOfficerName(patrol.getOfficerName());
                });

        return dto;
    }

    // =========================================
    // SUMMARY DASHBOARD
    // =========================================
    public IncidentSummaryDTO getSummary() {
        List<Incident> incidents = incidentRepository.findAll();

        int active = 0;
        int inProgress = 0;
        int closed = 0;

        for (Incident incident : incidents) {
            if (incident.getStatus() == IncidentStatus.ACTIVE) {
                active++;
            }
            else if (incident.getStatus() == IncidentStatus.IN_PROGRESS) {
                inProgress++;
            }
            else if (incident.getStatus() == IncidentStatus.CLOSED) {
                closed++;
            }
        }

        return new IncidentSummaryDTO(
                active,
                inProgress,
                closed,
                incidents.size()
        );
    }

    // =========================================
    // UPDATE STATUS
    // =========================================
    public Incident updateStatus(Long id, IncidentStatus newStatus) {
        Incident incident = getIncidentById(id);

        if (newStatus == IncidentStatus.CLOSED) {
            logger.warn(
                    "Intento de cerrar el incidente {} a través de updateStatus en lugar del endpoint de cierre.",
                    id
            );
            throw new RuntimeException(
                    "Use close endpoint to close incidents"
            );
        }

        logger.debug(
                "Actualizando estado del incidente {} a {}",
                id,
                newStatus
        );

        incident.setStatus(newStatus);
        Incident updated = incidentRepository.save(incident);

        logger.info(
                "Estado del incidente {} actualizado a {}",
                id,
                newStatus
        );

        return updated;
    }

    // =========================================
    // CLOSE INCIDENT (CORE OPERATIVO)
    // =========================================
    public Incident closeIncident(Long id) {
        // 1. Buscar incidente
        Incident incident = getIncidentById(id);

        // 2. Validar estado del incidente
        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            logger.warn(
                    "Intento de cerrar el incidente {} en estado {}",
                    id,
                    incident.getStatus()
            );
            throw new RuntimeException(
                    "Only IN_PROGRESS incidents can be closed"
            );
        }

        // 3. Buscar assignment relacionado
        Assignment assignment = assignmentRepository
                .findByIncident(incident)
                .orElseThrow(() -> {
                    logger.error(
                            "No se encontró la asignación para el incidente ID={}",
                            id
                    );
                    return new RuntimeException("Assignment not found");
                });

        // 4. Obtener patrulla
        Patrol patrol = assignment.getPatrol();

        // 5. Validar que la patrulla esté atendiendo
        if (patrol.getStatus() != PatrolStatus.BUSY) {
            logger.warn(
                    "Intento de cerrar incidente {} cuando la patrulla aún no estaba BUSY",
                    id
            );
            throw new RuntimeException(
                    "Incident can only be closed when patrol is BUSY"
            );
        }

        // 6. Liberar patrulla
        patrol.setStatus(PatrolStatus.AVAILABLE);

        // 7. Cerrar incidente
        incident.setStatus(IncidentStatus.CLOSED);
        incident.setClosedAt(java.time.LocalDateTime.now());

        // 8. Finalizar asignación
        assignment.setFinishedAt(java.time.LocalDateTime.now());

        // 9. Guardar cambios
        patrolRepository.save(patrol);
        assignmentRepository.save(assignment);
        Incident closedIncident = incidentRepository.save(incident);

        logger.info(
                "Incidente {} cerrado correctamente. Patrulla {} liberada.",
                incident.getId(),
                patrol.getCode()
        );

        return closedIncident;
    }

    // =========================================
    // RECENT CREATED (FEED)
    // =========================================
    public List<Incident> getRecentCreated() {
        return incidentRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // =========================================
    // RECENT UPDATED (FEED)
    // =========================================
    public List<Incident> getRecentUpdates() {
        return incidentRepository.findTop10ByOrderByUpdatedAtDesc();
    }
}