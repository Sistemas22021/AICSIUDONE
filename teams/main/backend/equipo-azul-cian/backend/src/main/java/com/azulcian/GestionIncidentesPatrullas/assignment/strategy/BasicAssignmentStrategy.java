package com.azulcian.GestionIncidentesPatrullas.assignment.strategy;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.repository.IncidentRepository;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.springframework.stereotype.Component;

@Component
public class BasicAssignmentStrategy implements AssignmentStrategy {

    private final AssignmentRepository assignmentRepository;
    private final IncidentRepository incidentRepository;
    private final PatrolRepository patrolRepository;

    public BasicAssignmentStrategy(
            AssignmentRepository assignmentRepository,
            IncidentRepository incidentRepository,
            PatrolRepository patrolRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.incidentRepository = incidentRepository;
        this.patrolRepository = patrolRepository;
    }

    @Override
    public Assignment execute(AssignmentRequestDTO dto) {

        Incident incident = incidentRepository.findById(dto.getIncidentId())
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        Patrol patrol = patrolRepository.findById(dto.getPatrolId())
                .orElseThrow(() -> new RuntimeException("Patrol not found"));

        if (assignmentRepository.findByIncident(incident).isPresent()) {
            throw new RuntimeException("Incident already has a patrol assigned");
        }

        if (incident.getStatus() != IncidentStatus.ACTIVE) {
            throw new RuntimeException("Incident must be ACTIVE");
        }

        if (patrol.getStatus() != PatrolStatus.AVAILABLE) {
            throw new RuntimeException("Patrol must be AVAILABLE");
        }

        incident.setStatus(IncidentStatus.IN_PROGRESS);
        patrol.setStatus(PatrolStatus.EN_ROUTE);

        incidentRepository.save(incident);
        patrolRepository.save(patrol);

        Assignment assignment = new Assignment(incident, patrol);

        return assignmentRepository.save(assignment);
    }
}
