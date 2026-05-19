package com.azulcian.GestionIncidentesPatrullas.incident.service;

import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentSummaryDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // =========================================
    // CREATE INCIDENT
    // =========================================
    public Incident createIncident(Incident incident) {
        incident.setStatus("ACTIVE");
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
            switch (incident.getStatus()) {
                case "ACTIVE" -> active++;
                case "IN_PROGRESS" -> inProgress++;
                case "CLOSED" -> closed++;
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
    public Incident updateStatus(Long id, String newStatus) {

        Incident incident = getIncidentById(id);

        if (!newStatus.equals("ACTIVE") &&
                !newStatus.equals("IN_PROGRESS") &&
                !newStatus.equals("CLOSED")) {
            throw new RuntimeException("Invalid status");
        }

        incident.setStatus(newStatus);

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