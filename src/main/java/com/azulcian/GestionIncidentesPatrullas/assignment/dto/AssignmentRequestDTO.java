package com.azulcian.GestionIncidentesPatrullas.assignment.dto;

public class AssignmentRequestDTO {

    private Long incidentId;
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