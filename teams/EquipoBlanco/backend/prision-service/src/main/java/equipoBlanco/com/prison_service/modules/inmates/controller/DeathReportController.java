package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingHandoverDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.DeathReportDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.InternalIncidentDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Belonging.BelongingStatus;
import equipoBlanco.com.prison_service.modules.inmates.service.BelongingService;
import equipoBlanco.com.prison_service.modules.inmates.service.DeathReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeathReportController {

    private final DeathReportService deathReportService;
    private final BelongingService belongingService;

    private void verifySupervisorRole(String role) {
        if (!"Supervisor".equalsIgnoreCase(role)) {
            throw new RuntimeException("Acceso denegado: Solo el rol de Supervisor está autorizado para esta operación.");
        }
    }

    @PostMapping("/inmates/{id}/death-report/natural")
    public ResponseEntity<DeathReportDto> registerNaturalDeath(
            @PathVariable UUID id,
            @RequestBody DeathReportDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.registerNaturalDeath(id, dto, username));
    }

    @PostMapping("/inmates/{id}/death-report/non-natural")
    public ResponseEntity<DeathReportDto> registerNonNaturalDeathDraft(
            @PathVariable UUID id,
            @RequestBody DeathReportDto dto,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.registerNonNaturalDeathDraft(id, dto));
    }

    @PostMapping("/incidents")
    public ResponseEntity<InternalIncidentDto> createIncident(
            @RequestBody InternalIncidentDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.createIncidentAndFinalize(dto, username));
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<InternalIncidentDto>> getAllIncidents(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.getAllIncidents());
    }

    @GetMapping("/incidents/{id}")
    public ResponseEntity<InternalIncidentDto> getIncidentById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.getIncidentById(id));
    }

    @GetMapping("/inmates/{id}/death-report")
    public ResponseEntity<DeathReportDto> getDeathReportByInmate(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(deathReportService.getDeathReportByInmate(id));
    }

    @PostMapping("/inmates/{id}/belongings/handover")
    public ResponseEntity<BelongingHandoverDto> handoverBelongings(
            @PathVariable UUID id,
            @RequestBody BelongingHandoverDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(belongingService.handoverBelongings(id, dto, username));
    }

    @GetMapping("/inmates/{id}/belongings/handovers")
    public ResponseEntity<List<BelongingHandoverDto>> getBelongingHandovers(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(belongingService.getHandoversByInmate(id));
    }

    @PutMapping("/belongings/{id}/status")
    public ResponseEntity<BelongingDto> toggleBelongingStatus(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verifySupervisorRole(role);
        return ResponseEntity.ok(belongingService.toggleRetainedStatus(id));
    }
}
