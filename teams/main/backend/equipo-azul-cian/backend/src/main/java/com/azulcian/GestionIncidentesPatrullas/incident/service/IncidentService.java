package com.azulcian.GestionIncidentesPatrullas.incident.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentSummaryDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.repository.IncidentRepository;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

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
        incident.setStatus(IncidentStatus.ACTIVE);
        return incidentRepository.save(incident);
    }

    // =========================================
    // LIST RECENT INCIDENTS
    // =========================================
    public List<Incident> getAllIncidents() {
        return incidentRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // =========================================
    // GET BY ID
    // =========================================
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));
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

        incident.setStatus(newStatus);

        return incidentRepository.save(incident);
    }

    // =========================================
    // CLOSE INCIDENT (CORE OPERATIVO)
    // =========================================
    public Incident closeIncident(Long id) {

        // 1. Buscar incidente
        Incident incident = getIncidentById(id);

        // 2. Validar estado
        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "Only IN_PROGRESS incidents can be closed"
            );
        }

        // 3. Buscar assignment relacionado
        Assignment assignment = assignmentRepository
                .findByIncident(incident)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found")
                );

        // 4. Obtener patrulla
        Patrol patrol = assignment.getPatrol();

        // 5. Liberar patrulla
        patrol.setStatus(PatrolStatus.AVAILABLE);

        // 6. Cerrar incidente
        incident.setStatus(IncidentStatus.CLOSED);

        // 7. Marcar asignación como finalizada
        assignment.setFinishedAt(java.time.LocalDateTime.now());

        // 8. Guardar cambios
        patrolRepository.save(patrol);
        assignmentRepository.save(assignment);

        return incidentRepository.save(incident);
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