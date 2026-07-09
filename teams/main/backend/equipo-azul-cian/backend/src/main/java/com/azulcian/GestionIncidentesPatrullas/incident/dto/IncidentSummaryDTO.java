package com.azulcian.GestionIncidentesPatrullas.incident.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "IncidentSummary",
        description = "Resumen estadístico de los incidentes registrados en el sistema."
)
public class IncidentSummaryDTO {

    @Schema(
            description = "Cantidad de incidentes activos",
            example = "5"
    )
    private int active;

    @Schema(
            description = "Cantidad de incidentes en atención",
            example = "2"
    )
    private int inProgress;

    @Schema(
            description = "Cantidad de incidentes cerrados",
            example = "8"
    )
    private int closed;

    @Schema(
            description = "Cantidad total de incidentes registrados",
            example = "15"
    )
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
