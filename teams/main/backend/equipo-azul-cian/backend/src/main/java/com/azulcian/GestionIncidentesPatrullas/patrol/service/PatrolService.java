package com.azulcian.GestionIncidentesPatrullas.patrol.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatrolService {

    private static final Logger logger = LoggerFactory.getLogger(PatrolService.class);

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

        logger.debug(
                "Registrando nueva patrulla con código={}",
                patrol.getCode()
        );

        // Si no viene estado, asignar AVAILABLE
        if (patrol.getStatus() == null) {
            patrol.setStatus(PatrolStatus.AVAILABLE);
        }

        // Validar estados permitidos para el registro
        if (patrol.getStatus() != PatrolStatus.AVAILABLE &&
                patrol.getStatus() != PatrolStatus.OUT_OF_SERVICE) {

            logger.warn(
                    "Intento de registrar patrulla {} con estado inválido {}",
                    patrol.getCode(),
                    patrol.getStatus()
            );

            throw new RuntimeException(
                    "Una patrulla solo puede registrarse como AVAILABLE o OUT_OF_SERVICE."
            );
        }

        Patrol saved = patrolRepository.save(patrol);

        logger.info(
                "Patrulla registrada. Código={}, Estado={}",
                saved.getCode(),
                saved.getStatus()
        );

        return saved;
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

        logger.debug(
                "Buscando patrulla con ID={}",
                id
        );

        return patrolRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(
                            "No existe la patrulla con ID={}",
                            id
                    );
                    return new RuntimeException("No se encontró la patrulla con el ID: " + id);
                });
    }

    // =========================================
    // UPDATE PATROL STATUS (ADMINISTRATIVE)
    // =========================================
    public Patrol updateStatus(Long id, PatrolStatus newStatus) {

        Patrol patrol = getPatrolById(id);
        PatrolStatus currentStatus = patrol.getStatus();

        // Estados operativos automáticos NO deben cambiarse manualmente
        if (currentStatus == PatrolStatus.EN_ROUTE || currentStatus == PatrolStatus.BUSY) {
            logger.warn(
                    "Intento de modificar manualmente la patrulla {} estando en estado {}",
                    patrol.getCode(),
                    currentStatus
            );
            throw new RuntimeException(
                    "Las patrullas EN_ROUTE o BUSY no pueden cambiar su estado manualmente."
            );
        }

        // Solo permitir gestión administrativa
        if (newStatus != PatrolStatus.AVAILABLE && newStatus != PatrolStatus.OUT_OF_SERVICE) {
            logger.warn(
                    "Intento de cambio manual no permitido a estado {} para la patrulla {}",
                    newStatus,
                    patrol.getCode()
            );
            throw new RuntimeException(
                    "El cambio manual solo permite AVAILABLE u OUT_OF_SERVICE."
            );
        }

        patrol.setStatus(newStatus);

        Patrol updated = patrolRepository.save(patrol);

        logger.info(
                "Patrulla {} cambió de estado a {}",
                updated.getCode(),
                updated.getStatus()
        );

        return updated;
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
            logger.warn(
                    "La patrulla {} intentó marcar llegada estando en estado {}",
                    patrol.getCode(),
                    patrol.getStatus()
            );
            throw new RuntimeException(
                    "Solo las patrullas en ruta (EN_ROUTE) pueden marcar su llegada."
            );
        }

        // Buscar la asignación activa usando el repositorio inyectado
        Assignment assignment = assignmentRepository
                .findByPatrolAndFinishedAtIsNull(patrol)
                .orElseThrow(() -> {
                    logger.error(
                            "La patrulla {} no tiene una asignación activa",
                            patrol.getCode()
                    );
                    return new RuntimeException("No se encontró una asignación activa para esta patrulla.");
                });

        Incident incident = assignment.getIncident();

        // Validar que el incidente esté en progreso antes de atenderse
        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            logger.warn(
                    "La patrulla {} intentó atender un incidente que no estaba IN_PROGRESS",
                    patrol.getCode()
            );
            throw new RuntimeException(
                    "El incidente debe estar IN_PROGRESS antes de atenderse."
            );
        }

        // Simular llegada al lugar del incidente
        patrol.setLatitude(incident.getLatitude());
        patrol.setLongitude(incident.getLongitude());

        // Cambiar estado operativo a ocupado
        patrol.setStatus(PatrolStatus.BUSY);

        Patrol updated = patrolRepository.save(patrol);

        logger.info(
                "Patrulla {} llegó al incidente {} y cambió a estado BUSY",
                updated.getCode(),
                incident.getId()
        );

        return updated;
    }
}