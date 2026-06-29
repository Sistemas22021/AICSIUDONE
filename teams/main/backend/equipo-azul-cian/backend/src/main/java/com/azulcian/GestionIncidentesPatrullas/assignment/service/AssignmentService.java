package com.azulcian.GestionIncidentesPatrullas.assignment.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.repository.IncidentRepository;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final IncidentRepository incidentRepository;
    private final PatrolRepository patrolRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            IncidentRepository incidentRepository,
            PatrolRepository patrolRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.incidentRepository = incidentRepository;
        this.patrolRepository = patrolRepository;
    }

    public Assignment assign(AssignmentRequestDTO dto) {

        // 1. Buscar entidades
        Incident incident = incidentRepository.findById(dto.getIncidentId())
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        Patrol patrol = patrolRepository.findById(dto.getPatrolId())
                .orElseThrow(() -> new RuntimeException("Patrol not found"));

        // 2. VALIDACIÓN: incidente ya asignado
        if (assignmentRepository.findByIncident(incident).isPresent()) {
            throw new RuntimeException("Incident already has a patrol assigned");
        }

        // 3. VALIDACIONES DE ESTADO
        if (incident.getStatus() != IncidentStatus.ACTIVE) {
            throw new RuntimeException("Incident must be ACTIVE");
        }

        if (patrol.getStatus() != PatrolStatus.AVAILABLE) {
            throw new RuntimeException("Patrol must be AVAILABLE");
        }

        // 4. CAMBIOS AUTOMÁTICOS (CORE DEL SISTEMA)
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        patrol.setStatus(PatrolStatus.EN_ROUTE);

        incidentRepository.save(incident);
        patrolRepository.save(patrol);

        // 5. CREAR ASIGNACIÓN
        Assignment assignment = new Assignment(incident, patrol);

        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findByFinishedAtIsNull();
    }
}