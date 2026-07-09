package com.azulcian.GestionIncidentesPatrullas.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AssignmentRequest",
        description = "Solicitud para asignar una patrulla disponible a un incidente activo."
)
public class AssignmentRequestDTO {

    @Schema(
            description = "Identificador del incidente que recibirá la asignación",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long incidentId;

    @Schema(
            description = "Identificador de la patrulla disponible que atenderá el incidente",
            example = "4",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long patrolId;

    public AssignmentRequestDTO() {}

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public Long getPatrolId() {
        return patrolId;
    }

    public void setPatrolId(Long patrolId) {
        this.patrolId = patrolId;
    }
}