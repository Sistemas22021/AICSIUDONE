package com.azulcian.GestionIncidentesPatrullas.incident.controller;

import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentDetailDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.dto.IncidentSummaryDTO;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.service.IncidentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_CENTRO_MANDO', 'ROLE_OFICIAL_PATRULLA')")
@Tag(
        name = "Incidentes",
        description = "Gestión de incidentes: creación, consulta, actualización de estado y cierre operativo"
)
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // =========================================
    // CREATE INCIDENT
    // =========================================
    @Operation(
            summary = "Crear incidente",
            description = "Registra un nuevo incidente en el sistema. El estado inicial es ACTIVE automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidente creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Incident createIncident(@RequestBody Incident incident) {
        return incidentService.createIncident(incident);
    }

    // =========================================
    // LIST INCIDENTS
    // =========================================
    @Operation(
            summary = "Listar incidentes",
            description = "Obtiene los últimos 10 incidentes con sus detalles completos y patrullas asignadas."
    )
    @ApiResponse(responseCode = "200", description = "Lista de incidentes obtenida correctamente")
    @GetMapping
    public List<IncidentDetailDTO> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    // =========================================
    // GET BY ID
    // =========================================
    @Operation(
            summary = "Obtener incidente por ID",
            description = "Devuelve el detalle completo de un incidente específico junto con los datos de la patrulla si está asignada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidente encontrado"),
            @ApiResponse(responseCode = "404", description = "Incidente no encontrado")
    })
    @GetMapping("/{id}")
    public IncidentDetailDTO getById(@PathVariable Long id) {
        return incidentService.getIncidentDetailById(id);
    }

    // =========================================
    // SUMMARY DASHBOARD
    // =========================================
    @Operation(
            summary = "Resumen del dashboard",
            description = "Devuelve estadísticas de incidentes por estado (ACTIVE, IN_PROGRESS, CLOSED)"
    )
    @ApiResponse(responseCode = "200", description = "Resumen generado correctamente")
    @GetMapping("/summary")
    public IncidentSummaryDTO getSummary() {
        return incidentService.getSummary();
    }

    // =========================================
    // RECENT CREATED
    // =========================================
    @Operation(
            summary = "Incidentes creados recientemente",
            description = "Lista los últimos incidentes registrados en el sistema"
    )
    @GetMapping("/recent/created")
    public List<Incident> getRecentCreated() {
        return incidentService.getRecentCreated();
    }

    // =========================================
    // RECENT UPDATED
    // =========================================
    @Operation(
            summary = "Incidentes actualizados recientemente",
            description = "Lista los últimos incidentes modificados"
    )
    @GetMapping("/recent/updated")
    public List<Incident> getRecentUpdates() {
        return incidentService.getRecentUpdates();
    }

    // =========================================
    // UPDATE STATUS
    // =========================================
    @Operation(
            summary = "Actualizar estado de incidente",
            description = "Permite cambiar manualmente el estado del incidente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Incidente no encontrado")
    })
    @PatchMapping("/{id}/status")
    public Incident updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        return incidentService.updateStatus(id, request.status);
    }

    // =========================================
    // CLOSE INCIDENT
    // =========================================
    @Operation(
            summary = "Cerrar incidente",
            description = "Cierra el incidente y ejecuta la lógica de liberación de patrulla asignada"
    )
    @ApiResponse(responseCode = "200", description = "Incidente cerrado correctamente")
    @PatchMapping("/{id}/close")
    public Incident closeIncident(@PathVariable Long id) {
        return incidentService.closeIncident(id);
    }

    // =========================================
    // DTO INTERNO
    // =========================================
    public static class StatusRequest {
        public IncidentStatus status;
    }
}