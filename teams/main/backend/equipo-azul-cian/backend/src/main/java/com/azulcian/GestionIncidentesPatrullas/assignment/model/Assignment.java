package com.azulcian.GestionIncidentesPatrullas.assignment.model;

import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con incidente
    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    // Relación con patrulla
    @ManyToOne
    @JoinColumn(name = "patrol_id", nullable = false)
    private Patrol patrol;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public Assignment() {}

    public Assignment(Incident incident, Patrol patrol) {
        this.incident = incident;
        this.patrol = patrol;
    }

    @PrePersist
    public void prePersist() {
        this.assignedAt = LocalDateTime.now();
    }

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Patrol getPatrol() {
        return patrol;
    }

    public void setPatrol(Patrol patrol) {
        this.patrol = patrol;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}