package com.azulcian.GestionIncidentesPatrullas.incident.dto;

import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "IncidentDetail",
        description = "Detalle completo de un incidente incluyendo la patrulla asignada."
)
public class IncidentDetailDTO {

    // --- Datos del Incidente ---
    @Schema(
            description = "Identificador único del incidente",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Tipo de incidente",
            example = "Robo"
    )
    private String type;

    @Schema(
            description = "Descripción detallada del incidente",
            example = "Robo en un establecimiento comercial."
    )
    private String description;

    @Schema(
            description = "Latitud del incidente",
            example = "11.0123"
    )
    private Double latitude;

    @Schema(
            description = "Longitud del incidente",
            example = "-63.9123"
    )
    private Double longitude;

    @Schema(
            description = "Estado actual del incidente",
            example = "IN_PROGRESS"
    )
    private IncidentStatus status;

    // --- Prioridad ---
    @Schema(
            description = "Prioridad asignada al incidente",
            example = "ALTA"
    )
    private String priority;

    // --- Fechas ---
    @Schema(
            description = "Fecha y hora de creación del incidente",
            example = "2026-07-07T10:15:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Fecha y hora de la última actualización del incidente",
            example = "2026-07-07T11:00:00"
    )
    private LocalDateTime updatedAt;

    @Schema(
            description = "Fecha y hora de cierre del incidente",
            example = "2026-07-07T12:30:00"
    )
    private LocalDateTime closedAt;

    // --- Patrulla Asignada ---
    @Schema(
            description = "Código de la patrulla asignada",
            example = "P-04"
    )
    private String patrolCode;

    @Schema(
            description = "Nombre del oficial responsable de la patrulla",
            example = "Carlos Rodríguez"
    )
    private String patrolOfficerName;


    // Constructor vacío obligatorio para Jackson/Spring
    public IncidentDetailDTO() {
    }


    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getPatrolCode() {
        return patrolCode;
    }

    public void setPatrolCode(String patrolCode) {
        this.patrolCode = patrolCode;
    }

    public String getPatrolOfficerName() {
        return patrolOfficerName;
    }

    public void setPatrolOfficerName(String patrolOfficerName) {
        this.patrolOfficerName = patrolOfficerName;
    }
}
