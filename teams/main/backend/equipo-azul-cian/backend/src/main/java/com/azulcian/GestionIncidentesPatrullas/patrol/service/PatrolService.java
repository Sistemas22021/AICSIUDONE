package com.azulcian.GestionIncidentesPatrullas.patrol.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatrolService {

    private final PatrolRepository patrolRepository;
    private final AssignmentRepository assignmentRepository;

    // Inyección de dependencias por constructor
    public PatrolService(PatrolRepository patrolRepository, AssignmentRepository assignmentRepository) {
        this.patrolRepository = patrolRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // =========================================
    // CREATE PATROL
    // =========================================
    public Patrol createPatrol(Patrol patrol) {

        // Estado por defecto
        if (patrol.getStatus() == null) {
            patrol.setStatus(PatrolStatus.AVAILABLE);
        }

        return patrolRepository.save(patrol);
    }

    // =========================================
    // GET ALL PATROLS
    // =========================================
    public List<Patrol> getAllPatrols() {
        return patrolRepository.findAll();
    }

    // =========================================
    // GET PATROL BY ID
    // =========================================
    public Patrol getPatrolById(Long id) {

        return patrolRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró la patrulla con el ID: " + id));
    }

    // =========================================
    // UPDATE PATROL STATUS (ADMINISTRATIVE)
    // =========================================
    public Patrol updateStatus(Long id, PatrolStatus newStatus) {

        Patrol patrol = getPatrolById(id);
        PatrolStatus currentStatus = patrol.getStatus();

        // Estados operativos automáticos NO deben cambiarse manualmente
        if (currentStatus == PatrolStatus.EN_ROUTE || currentStatus == PatrolStatus.BUSY) {
            throw new RuntimeException(
                    "Las patrullas EN_ROUTE o BUSY no pueden cambiar su estado manualmente."
            );
        }

        // Solo permitir gestión administrativa
        if (newStatus != PatrolStatus.AVAILABLE && newStatus != PatrolStatus.OUT_OF_SERVICE) {
            throw new RuntimeException(
                    "El cambio manual solo permite AVAILABLE u OUT_OF_SERVICE."
            );
        }

        patrol.setStatus(newStatus);

        return patrolRepository.save(patrol);
    }

    // =========================================
    // GET AVAILABLE PATROLS
    // =========================================
    public List<Patrol> getAvailablePatrols() {
        return patrolRepository.findByStatus(PatrolStatus.AVAILABLE);
    }

    // =========================================
    // MARK PATROL AS ARRIVED
    // =========================================
    public Patrol markAsArrived(Long id) {

        Patrol patrol = getPatrolById(id);

        // Controlar que solo las patrullas en camino puedan marcar llegada
        if (patrol.getStatus() != PatrolStatus.EN_ROUTE) {
            throw new RuntimeException(
                    "Solo las patrullas en ruta (EN_ROUTE) pueden marcar su llegada."
            );
        }

        // Buscar la asignación activa usando el repositorio inyectado
        Assignment assignment = assignmentRepository
                .findByPatrolAndFinishedAtIsNull(patrol)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró una asignación activa para esta patrulla.")
                );

        Incident incident = assignment.getIncident();

        // Cambio 2: Validar que el incidente esté en progreso antes de atenderse
        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "El incidente debe estar IN_PROGRESS antes de atenderse."
            );
        }

        // Simular llegada al lugar del incidente
        patrol.setLatitude(incident.getLatitude());
        patrol.setLongitude(incident.getLongitude());

        // Cambiar estado operativo a ocupado
        patrol.setStatus(PatrolStatus.BUSY);

        return patrolRepository.save(patrol);
    }
}