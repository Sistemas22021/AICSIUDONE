package com.azulcian.GestionIncidentesPatrullas.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Schema(
        name = "Incident",
        description = "Representa un incidente reportado al sistema de gestión de incidentes."
)
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del incidente",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(name = "type", nullable = false)
    @Schema(
            description = "Tipo de incidente reportado",
            example = "ROBO",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String type;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @Schema(
            description = "Descripción detallada del incidente",
            example = "Robo en zona comercial con dos sospechosos",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;

    @Column(name = "latitude", nullable = false)
    @Schema(
            description = "Latitud donde ocurrió el incidente",
            example = "4.6100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    @Schema(
            description = "Longitud donde ocurrió el incidente",
            example = "-74.0800",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(
            description = "Estado actual del incidente",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "IN_PROGRESS", "CLOSED"}
    )
    private IncidentStatus status;

    @Column(name = "priority", nullable = false)
    @Schema(
            description = "Nivel de prioridad del incidente",
            example = "HIGH",
            allowableValues = {"LOW", "MEDIUM", "HIGH"}
    )
    private String priority;

    @Column(name = "created_at", updatable = false)
    @Schema(
            description = "Fecha y hora de creación del incidente",
            example = "2026-07-03T18:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(
            description = "Fecha y hora de la última actualización",
            example = "2026-07-03T18:45:12",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    @Schema(
            description = "Fecha y hora de cierre del incidente",
            example = "2026-07-03T19:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime closedAt;

    public Incident() {}

    public Incident(Long id, String type, String description,
                    Double latitude, Double longitude,
                    IncidentStatus status, String priority) {

        this.id = id;
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.priority = priority;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = IncidentStatus.ACTIVE;
        }

        if (this.priority == null) {
            this.priority = "MEDIUM";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // GETTERS & SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}