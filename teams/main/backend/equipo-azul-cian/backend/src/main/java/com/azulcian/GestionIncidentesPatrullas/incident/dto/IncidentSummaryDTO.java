package com.azulcian.GestionIncidentesPatrullas.incident.dto;

public class IncidentSummaryDTO {

    private int active;
    private int inProgress;
    private int closed;
    private int total;

    public IncidentSummaryDTO(int active,
                              int inProgress,
                              int closed,
                              int total) {

        this.active = active;
        this.inProgress = inProgress;
        this.closed = closed;
        this.total = total;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
