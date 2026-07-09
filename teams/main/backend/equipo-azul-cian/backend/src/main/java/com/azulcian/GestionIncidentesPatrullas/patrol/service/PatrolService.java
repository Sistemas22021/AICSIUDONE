package com.azulcian.GestionIncidentesPatrullas.patrol.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
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
    // UPDATE PATROL STATUS
    // =========================================
    public Patrol updateStatus(Long id, PatrolStatus newStatus) {

        Patrol patrol = getPatrolById(id);

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

        // Simular llegada al lugar del incidente
        patrol.setLatitude(incident.getLatitude());
        patrol.setLongitude(incident.getLongitude());

        // Cambiar estado operativo a ocupado
        patrol.setStatus(PatrolStatus.BUSY);

        return patrolRepository.save(patrol);
    }
}