package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingHandoverDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.DeathReportDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.InternalIncidentDto;
import equipoBlanco.com.prison_service.modules.inmates.service.BelongingService;
import equipoBlanco.com.prison_service.modules.inmates.service.DeathReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Incidentes y Fallecimientos", description = "Gestión de reportes de fallecimiento, incidentes internos y entrega de pertenencias")
public class DeathReportController {

    private final DeathReportService deathReportService;
    private final BelongingService belongingService;

    private void verifySupervisorRole(String role) {
        if (!"Supervisor".equalsIgnoreCase(role)) {
            throw new RuntimeException("Acceso denegado: Solo el rol de Supervisor está autorizado para esta operación.");
        }
    }

    @PostMapping("/inmates/{id}/death-report/natural")
    @Operation(summary = "Registrar fallecimiento natural", description = "Registra un reporte de fallecimiento por causa natural de un recluso")
    public ResponseEntity<DeathReportDto> registerNaturalDeath(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody DeathReportDto dto,
            @Parameter(description = "Nombre del supervisor") @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.registerNaturalDeath(id, dto, username));
    }

    @PostMapping("/inmates/{id}/death-report/non-natural")
    @Operation(summary = "Registrar borrador de fallecimiento no natural", description = "Registra un borrador de reporte de fallecimiento por causa no natural (requiere incidente posterior)")
    public ResponseEntity<DeathReportDto> registerNonNaturalDeathDraft(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody DeathReportDto dto,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.registerNonNaturalDeathDraft(id, dto));
    }

    @PostMapping("/incidents")
    @Operation(summary = "Crear incidente interno", description = "Crea un incidente interno y finaliza el proceso (asociado a fallecimiento no natural)")
    public ResponseEntity<InternalIncidentDto> createIncident(
            @RequestBody InternalIncidentDto dto,
            @Parameter(description = "Nombre del supervisor") @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.createIncidentAndFinalize(dto, username));
    }

    @GetMapping("/incidents")
    @Operation(summary = "Listar incidentes", description = "Obtiene todos los incidentes internos registrados")
    public ResponseEntity<List<InternalIncidentDto>> getAllIncidents(
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.getAllIncidents());
    }

    @GetMapping("/incidents/{id}")
    @Operation(summary = "Obtener incidente por ID", description = "Obtiene los detalles de un incidente interno específico")
    public ResponseEntity<InternalIncidentDto> getIncidentById(
            @Parameter(description = "UUID del incidente") @PathVariable UUID id,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.getIncidentById(id));
    }

    @GetMapping("/inmates/{id}/death-report")
    @Operation(summary = "Obtener reporte de fallecimiento", description = "Obtiene el reporte de fallecimiento de un recluso específico")
    public ResponseEntity<DeathReportDto> getDeathReportByInmate(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.getDeathReportByInmate(id));
    }

    @PostMapping("/inmates/{id}/belongings/handover")
    @Operation(summary = "Entregar pertenencias", description = "Registra la entrega de pertenencias de un recluso fallecido a un familiar")
    public ResponseEntity<BelongingHandoverDto> handoverBelongings(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody BelongingHandoverDto dto,
            @Parameter(description = "Nombre del supervisor") @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(belongingService.handoverBelongings(id, dto, username));
    }

    @GetMapping("/inmates/{id}/belongings/handovers")
    @Operation(summary = "Historial de entregas de pertenencias", description = "Obtiene el historial de entregas de pertenencias de un recluso")
    public ResponseEntity<List<BelongingHandoverDto>> getBelongingHandovers(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(belongingService.getHandoversByInmate(id));
    }

    @PutMapping("/belongings/{id}/status")
    @Operation(summary = "Cambiar estado de pertenencia", description = "Cambia el estado de retención de una pertenencia (retenido/entregado)")
    public ResponseEntity<BelongingDto> toggleBelongingStatus(
            @Parameter(description = "UUID de la pertenencia") @PathVariable UUID id,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(belongingService.toggleRetainedStatus(id));
    }
}
