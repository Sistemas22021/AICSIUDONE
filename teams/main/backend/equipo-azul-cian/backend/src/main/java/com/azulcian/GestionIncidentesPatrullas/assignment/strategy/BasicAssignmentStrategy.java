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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia de asignación básica.
 * Cumple con LSP al implementar el contrato de AssignmentStrategy, permitiendo intercambiar estrategias sin alterar la lógica global.
 */
@Component
public class BasicAssignmentStrategy implements AssignmentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BasicAssignmentStrategy.class);

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

        logger.debug(
                "Iniciando asignación. Incidente={}, Patrulla={}",
                dto.getIncidentId(),
                dto.getPatrolId()
        );

        Incident incident = incidentRepository.findById(dto.getIncidentId())
                .orElseThrow(() -> {
                    logger.error(
                            "No existe el incidente con ID={}",
                            dto.getIncidentId()
                    );
                    return new RuntimeException("Incident not found");
                });

        Patrol patrol = patrolRepository.findById(dto.getPatrolId())
                .orElseThrow(() -> {
                    logger.error(
                            "No existe la patrulla con ID={}",
                            dto.getPatrolId()
                    );
                    return new RuntimeException("Patrol not found");
                });

        if (assignmentRepository.findByIncident(incident).isPresent()) {
            logger.warn(
                    "Intento de asignar nuevamente el incidente {}",
                    incident.getId()
            );
            throw new RuntimeException("Incident already has a patrol assigned");
        }

        if (incident.getStatus() != IncidentStatus.ACTIVE) {
            logger.warn(
                    "El incidente {} no está ACTIVE. Estado actual={}",
                    incident.getId(),
                    incident.getStatus()
            );
            throw new RuntimeException("Incident must be ACTIVE");
        }

        if (patrol.getStatus() != PatrolStatus.AVAILABLE) {
            logger.warn(
                    "La patrulla {} no está disponible. Estado actual={}",
                    patrol.getCode(),
                    patrol.getStatus()
            );
            throw new RuntimeException("Patrol must be AVAILABLE");
        }

        incident.setStatus(IncidentStatus.IN_PROGRESS);
        patrol.setStatus(PatrolStatus.EN_ROUTE);

        incidentRepository.save(incident);
        patrolRepository.save(patrol);

        Assignment assignment = new Assignment(incident, patrol);

        Assignment saved = assignmentRepository.save(assignment);

        logger.info(
                "Patrulla {} asignada correctamente al incidente {}",
                patrol.getCode(),
                incident.getId()
        );

        return saved;
    }
}