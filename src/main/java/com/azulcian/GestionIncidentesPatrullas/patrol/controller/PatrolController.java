package com.azulcian.GestionIncidentesPatrullas.patrol.controller;

import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.service.PatrolService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patrols")
public class PatrolController {

    private final PatrolService patrolService;

    public PatrolController(PatrolService patrolService) {
        this.patrolService = patrolService;
    }

    // =========================================
    // CREATE PATROL
    // =========================================
    @PostMapping
    public Patrol createPatrol(@RequestBody Patrol patrol) {
        return patrolService.createPatrol(patrol);
    }

    // =========================================
    // GET ALL PATROLS
    // =========================================
    @GetMapping
    public List<Patrol> getAllPatrols() {
        return patrolService.getAllPatrols();
    }

    // =========================================
    // GET AVAILABLE PATROLS
    // =========================================
    @GetMapping("/available")
    public List<Patrol> getAvailablePatrols() {
        return patrolService.getAvailablePatrols();
    }

    // =========================================
    // GET PATROL BY ID
    // =========================================
    @GetMapping("/{id}")
    public Patrol getPatrolById(@PathVariable Long id) {
        return patrolService.getPatrolById(id);
    }

    // =========================================
    // UPDATE PATROL STATUS
    // =========================================
    @PatchMapping("/{id}/status")
    public Patrol updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request
    ) {
        return patrolService.updateStatus(id, request.status);
    }

    // =========================================
    // DTO INTERNO
    // =========================================
    public static class StatusRequest {
        public PatrolStatus status;
    }
}