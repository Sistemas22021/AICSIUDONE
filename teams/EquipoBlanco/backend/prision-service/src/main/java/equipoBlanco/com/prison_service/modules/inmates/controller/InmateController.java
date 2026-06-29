package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.InmateDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.service.InmateService;
import equipoBlanco.com.prison_service.modules.inmates.dto.TemporaryEgressDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.TemporaryReturnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inmates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InmateController {

    private final InmateService inmateService;

    @GetMapping("/check-cedula/{cedula}")
    public ResponseEntity<Map<String, Boolean>> checkCedula(@PathVariable String cedula) {
        boolean exists = inmateService.cedulaHasActiveRecord(cedula);
        return ResponseEntity.ok(Map.of("hasActiveRecord", exists));
    }

    @PostMapping
    public ResponseEntity<InmateDto> register(@RequestBody InmateDto dto) {
        return ResponseEntity.ok(inmateService.register(dto));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<InmateDto>> getUnassigned() {
        return ResponseEntity.ok(inmateService.getByStatus(InmateStatus.ACTIVO_SIN_CELDA));
    }

    @GetMapping
    public ResponseEntity<List<InmateDto>> getAll() {
        return ResponseEntity.ok(inmateService.getAllInmates());
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<InmateDto>> getByStatus(@PathVariable InmateStatus status) {
        return ResponseEntity.ok(inmateService.getByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InmateDto> getByIdOrCedula(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return ResponseEntity.ok(inmateService.getInmateById(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(inmateService.getInmateByCedula(id));
        }
    }

    @GetMapping("/cell/{cellId}")
    public ResponseEntity<List<InmateDto>> getByCell(@PathVariable UUID cellId) {
        return ResponseEntity.ok(inmateService.getInmatesByCell(cellId));
    }

    @PostMapping("/{id}/discharge")
    public ResponseEntity<InmateDto> discharge(@PathVariable UUID id, @RequestBody equipoBlanco.com.prison_service.modules.inmates.dto.DischargeDto dto) {
        return ResponseEntity.ok(inmateService.dischargeInmate(id, dto));
    }

    @PostMapping("/{id}/temporary-egress")
    public ResponseEntity<InmateDto> registerTemporaryEgress(
            @PathVariable UUID id,
            @RequestBody TemporaryEgressDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(inmateService.registerTemporaryEgress(id, dto, username));
    }

    @PostMapping("/{id}/temporary-return")
    public ResponseEntity<InmateDto> registerTemporaryReturn(
            @PathVariable UUID id,
            @RequestBody TemporaryReturnDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(inmateService.registerTemporaryReturn(id, dto, username));
    }

    @PostMapping("/{id}/relocate-emergency")
    public ResponseEntity<InmateDto> relocateEmergency(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        if (!"Supervisor".equalsIgnoreCase(role)) {
            throw new RuntimeException("Acceso denegado: Solo el rol de Supervisor está autorizado para esta operación.");
        }
        UUID targetCellId = UUID.fromString(body.get("targetCellId"));
        return ResponseEntity.ok(inmateService.relocateEmergencyInmate(id, targetCellId, username));
    }
}
