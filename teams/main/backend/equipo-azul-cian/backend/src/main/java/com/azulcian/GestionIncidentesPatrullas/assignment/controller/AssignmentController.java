package com.azulcian.GestionIncidentesPatrullas.assignment.controller;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.service.AssignmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_CENTRO_MANDO')")
@Tag(
        name = "Asignaciones",
        description = "Gestión de asignación de patrullas a incidentes y ejecución del flujo operativo"
)

// =============================================================
// PRINCIPIO SOLID: S - Single Responsibility Principle (SRP)
// -------------------------------------------------------------
// Este controlador únicamente gestiona solicitudes HTTP y delega
// la lógica del CU-03 al AssignmentService.
//
// No contiene reglas de negocio ni acceso directo a datos.
//
// ✔ Mantiene una única responsabilidad.
// =============================================================
public class AssignmentController {

    // ==========================================================
    // Dependency Injection (Spring Framework)
    // ----------------------------------------------------------
    // El controlador recibe el servicio mediante inyección de
    // dependencias para delegar completamente la lógica del
    // negocio.
    // ==========================================================
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    // =========================================
    // CU-03: ASIGNACIÓN OPERATIVA
    // =========================================
    //
    // Este endpoint únicamente recibe la solicitud HTTP y
    // delega toda la ejecución del caso de uso al servicio.
    //
    // No realiza validaciones de negocio.
    // No modifica estados.
    // No crea asignaciones directamente.
    //
    // Todas esas responsabilidades pertenecen a AssignmentService.
    //
    @Operation(
            summary = "Asignar patrulla a incidente",
            description = "Asigna una patrulla disponible a un incidente activo. " +
                    "Ejecuta automáticamente la lógica de negocio: " +
                    "INCIDENTE → IN_PROGRESS y PATRULLA → EN_ROUTE"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Reglas de negocio no cumplidas (estado inválido)"),
            @ApiResponse(responseCode = "404", description = "Incidente o patrulla no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Assignment assignPatrol(@RequestBody AssignmentRequestDTO dto) {

        return assignmentService.assign(dto);
    }

    // =========================================
    // LIST ALL ASSIGNMENTS
    // =========================================
    @Operation(
            summary = "Listar asignaciones",
            description = "Obtiene el historial completo de asignaciones realizadas entre incidentes y patrullas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida correctamente")
    @GetMapping
    public List<Assignment> getAllAssignments() {

        return assignmentService.getAllAssignments();
    }
}