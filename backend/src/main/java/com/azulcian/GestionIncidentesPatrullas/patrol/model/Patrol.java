package com.azulcian.GestionIncidentesPatrullas.patrol.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patrols")
public class Patrol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "officer_name", nullable = false)
    private String officerName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PatrolStatus status;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Patrol() {}

    public Patrol(Long id, String code, String officerName,
                  Double latitude, Double longitude, PatrolStatus status) {
        this.id = id;
        this.code = code;
        this.officerName = officerName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        this.lastUpdated = LocalDateTime.now();

        if (this.status == null) {
            this.status = PatrolStatus.AVAILABLE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public PatrolStatus getStatus() {
        return status;
    }

    public void setStatus(PatrolStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
