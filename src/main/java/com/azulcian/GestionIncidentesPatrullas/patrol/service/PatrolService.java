package com.azulcian.GestionIncidentesPatrullas.patrol.service;

import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatrolService {

    private final PatrolRepository patrolRepository;

    public PatrolService(PatrolRepository patrolRepository) {
        this.patrolRepository = patrolRepository;
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
                        new RuntimeException("Patrol not found with id: " + id));
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
}