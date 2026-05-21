package com.azulcian.GestionIncidentesPatrullas.incident.controller;

import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentSummaryDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.service.IncidentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // =========================================
    // CREATE INCIDENT
    // =========================================
    @PostMapping
    public Incident createIncident(@RequestBody Incident incident) {
        return incidentService.createIncident(incident);
    }

    // =========================================
    // LIST RECENT INCIDENTS
    // =========================================
    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    // =========================================
    // GET BY ID
    // =========================================
    @GetMapping("/{id}")
    public Incident getById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    // =========================================
    // SUMMARY DASHBOARD
    // =========================================
    @GetMapping("/summary")
    public IncidentSummaryDTO getSummary() {
        return incidentService.getSummary();
    }

    // =========================================
    // RECENT CREATED
    // =========================================
    @GetMapping("/recent/created")
    public List<Incident> getRecentCreated() {
        return incidentService.getRecentCreated();
    }

    // =========================================
    // RECENT UPDATED
    // =========================================
    @GetMapping("/recent/updated")
    public List<Incident> getRecentUpdates() {
        return incidentService.getRecentUpdates();
    }

    // =========================================
    // UPDATE STATUS
    // =========================================
    @PatchMapping("/{id}/status")
    public Incident updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        return incidentService.updateStatus(id, request.status);
    }

    // =========================================
    // CLOSE INCIDENT
    // =========================================
    @PatchMapping("/{id}/close")
    public Incident closeIncident(@PathVariable Long id) {

        return incidentService.closeIncident(id);
    }

    // =========================================
    // DTO INTERNO
    // =========================================
    public static class StatusRequest {
        public IncidentStatus status;
    }
}