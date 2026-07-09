package com.azulcian.GestionIncidentesPatrullas.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Estados posibles de un incidente durante su ciclo de vida:
                - ACTIVE: El incidente ha sido registrado y está pendiente de atención.
                - IN_PROGRESS: Una patrulla ha sido asignada y el incidente está siendo atendido.
                - CLOSED: El incidente fue resuelto y finalizado.
                """
)
public enum IncidentStatus {

    ACTIVE,
    IN_PROGRESS,
    CLOSED
}