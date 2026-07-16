package com.azulcian.GestionIncidentesPatrullas.patrol.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "patrols")
@Schema(
        name = "Patrol",
        description = "Representa una patrulla policial disponible para atender incidentes."
)
public class Patrol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único de la patrulla",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    @Schema(
            description = "Código único que identifica a la patrulla",
            example = "PAT-001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String code;

    @Column(name = "officer_name", nullable = false)
    @Schema(
            description = "Nombre del oficial responsable de la patrulla",
            example = "Juan Pérez",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String officerName;

    @Column(name = "latitude")
    @Schema(
            description = "Latitud de la ubicación actual de la patrulla",
            example = "4.6200"
    )
    private Double latitude;

    @Column(name = "longitude")
    @Schema(
            description = "Longitud de la ubicación actual de la patrulla",
            example = "-74.0700"
    )
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(
            description = "Estado operativo actual de la patrulla",
            example = "AVAILABLE",
            allowableValues = {
                    "AVAILABLE",
                    "EN_ROUTE",
                    "BUSY"
            }
    )
    private PatrolStatus status;

    @Column(name = "last_updated")
    @Schema(
            description = "Fecha y hora de la última actualización de la patrulla",
            example = "2026-07-03T19:15:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
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
