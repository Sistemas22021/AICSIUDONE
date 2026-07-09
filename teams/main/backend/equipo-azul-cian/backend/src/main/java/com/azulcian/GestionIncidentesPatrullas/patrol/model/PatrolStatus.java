package com.azulcian.GestionIncidentesPatrullas.patrol.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Estados operativos de una patrulla:
                - AVAILABLE: La patrulla está disponible para recibir una asignación.
                - EN_ROUTE: La patrulla se encuentra en camino hacia el incidente asignado.
                - BUSY: La patrulla está atendiendo un incidente y no puede recibir nuevas asignaciones.
                - OUT_OF_SERVICE: La patrulla está fuera de servicio por mantenimiento u otra condición operativa.
                """
)
public enum PatrolStatus {

    AVAILABLE,
    EN_ROUTE,
    BUSY,
    OUT_OF_SERVICE
}