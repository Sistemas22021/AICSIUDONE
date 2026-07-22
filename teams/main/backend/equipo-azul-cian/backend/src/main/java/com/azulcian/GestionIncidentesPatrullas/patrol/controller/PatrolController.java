package com.azulcian.GestionIncidentesPatrullas.patrol.controller;

import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.service.PatrolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patrols")
@PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_CENTRO_MANDO')")
@Tag(
        name = "Patrullas",
        description = "Gestión de unidades policiales: creación, consulta y actualización de estado operativo"
)
public class PatrolController {

    private final PatrolService patrolService;

    public PatrolController(PatrolService patrolService) {
        this.patrolService = patrolService;
    }

    // =========================================
    // CREATE PATROL
    // =========================================
    @Operation(
            summary = "Crear patrulla",
            description = "Registra una nueva patrulla en el sistema con estado AVAILABLE por defecto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patrulla creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Patrol createPatrol(@RequestBody Patrol patrol) {
        return patrolService.createPatrol(patrol);
    }

    // =========================================
    // GET ALL PATROLS
    // =========================================
    @Operation(
            summary = "Listar patrullas",
            description = "Obtiene todas las patrullas registradas en el sistema"
    )
    @ApiResponse(responseCode = "200", description = "Lista de patrullas obtenida correctamente")
    @GetMapping
    public List<Patrol> getAllPatrols() {
        return patrolService.getAllPatrols();
    }

    // =========================================
    // GET AVAILABLE PATROLS
    // =========================================
    @Operation(
            summary = "Patrullas disponibles",
            description = "Retorna las patrullas en estado AVAILABLE listas para asignación"
    )
    @ApiResponse(responseCode = "200", description = "Patrullas disponibles obtenidas correctamente")
    @GetMapping("/available")
    public List<Patrol> getAvailablePatrols() {
        return patrolService.getAvailablePatrols();
    }

    // =========================================
    // GET BY ID
    // =========================================
    @Operation(
            summary = "Obtener patrulla por ID",
            description = "Devuelve el detalle completo de una patrulla específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patrulla encontrada"),
            @ApiResponse(responseCode = "404", description = "Patrulla no encontrada")
    })
    @GetMapping("/{id}")
    public Patrol getPatrolById(@PathVariable Long id) {
        return patrolService.getPatrolById(id);
    }

    // =========================================
    // UPDATE STATUS
    // =========================================
    @Operation(
            summary = "Actualizar estado de patrulla",
            description = """
                    Permite gestionar estados administrativos de una patrulla.
                    Solo permite cambiar entre AVAILABLE y OUT_OF_SERVICE.
                    Los estados EN_ROUTE y BUSY son gestionados automáticamente por el flujo operativo.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Patrulla no encontrada")
    })
    @PatchMapping("/{id}/status")
    public Patrol updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request
    ) {
        return patrolService.updateStatus(id, request.status);
    }

    // =========================================
    // MARK AS ARRIVED
    // =========================================
    @Operation(
            summary = "Marcar llegada de patrulla",
            description = "Indica que la patrulla llegó al incidente asignado. Actualiza su ubicación a la del incidente y cambia su estado a BUSY."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Llegada registrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Patrulla o asignación activa no encontrada"),
            @ApiResponse(responseCode = "400", description = "La patrulla no se encuentra en ruta")
    })
    @PatchMapping("/{id}/arrive")
    public Patrol markAsArrived(@PathVariable Long id) {
        return patrolService.markAsArrived(id);
    }

    // =========================================
    // DTO INTERNO
    // =========================================
    public static class StatusRequest {
        public PatrolStatus status;
    }
}