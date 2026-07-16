package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.InmateDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.service.InmateService;
import equipoBlanco.com.prison_service.modules.inmates.dto.TemporaryEgressDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.TemporaryReturnDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reclusos (Inmates)", description = "Gestión de reclusos: registro, consulta, egresos, traslados de emergencia, salidas y retornos temporales")
public class InmateController {

    private final InmateService inmateService;

    @GetMapping("/check-cedula/{cedula}")
    @Operation(summary = "Verificar cédula", description = "Verifica si una cédula tiene un registro activo en el sistema")
    public ResponseEntity<Map<String, Boolean>> checkCedula(
            @Parameter(description = "Número de cédula a verificar") @PathVariable String cedula) {
        boolean exists = inmateService.cedulaHasActiveRecord(cedula);
        return ResponseEntity.ok(Map.of("hasActiveRecord", exists));
    }

    @PostMapping
    @Operation(summary = "Registrar recluso", description = "Registra un nuevo recluso en el sistema con sus datos personales, foto y huellas")
    public ResponseEntity<InmateDto> register(@RequestBody InmateDto dto) {
        return ResponseEntity.ok(inmateService.register(dto));
    }

    @GetMapping("/unassigned")
    @Operation(summary = "Reclusos sin celda", description = "Obtiene todos los reclusos activos que no tienen una celda asignada")
    public ResponseEntity<List<InmateDto>> getUnassigned() {
        return ResponseEntity.ok(inmateService.getByStatus(InmateStatus.ACTIVO_SIN_CELDA));
    }

    @GetMapping
    @Operation(summary = "Listar reclusos", description = "Obtiene todos los reclusos registrados en el sistema")
    public ResponseEntity<List<InmateDto>> getAll() {
        return ResponseEntity.ok(inmateService.getAllInmates());
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Reclusos por estado", description = "Obtiene reclusos filtrados por su estado (ACTIVO_SIN_CELDA, ACTIVO_CON_CELDA, EGRESADO)")
    public ResponseEntity<List<InmateDto>> getByStatus(
            @Parameter(description = "Estado del recluso") @PathVariable InmateStatus status) {
        return ResponseEntity.ok(inmateService.getByStatus(status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener recluso por ID o cédula", description = "Obtiene un recluso por su UUID o por su número de cédula")
    public ResponseEntity<InmateDto> getByIdOrCedula(
            @Parameter(description = "UUID del recluso o número de cédula") @PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return ResponseEntity.ok(inmateService.getInmateById(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(inmateService.getInmateByCedula(id));
        }
    }

    @GetMapping("/cell/{cellId}")
    @Operation(summary = "Reclusos por celda", description = "Obtiene todos los reclusos asignados a una celda específica")
    public ResponseEntity<List<InmateDto>> getByCell(
            @Parameter(description = "UUID de la celda") @PathVariable UUID cellId) {
        return ResponseEntity.ok(inmateService.getInmatesByCell(cellId));
    }

    @PostMapping("/{id}/discharge")
    @Operation(summary = "Egresar recluso", description = "Registra el egreso (alta) de un recluso del sistema penitenciario con los datos del juzgado")
    public ResponseEntity<InmateDto> discharge(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody equipoBlanco.com.prison_service.modules.inmates.dto.DischargeDto dto) {
        return ResponseEntity.ok(inmateService.dischargeInmate(id, dto));
    }

    @PostMapping("/{id}/temporary-egress")
    @Operation(summary = "Salida temporal", description = "Registra una salida temporal de un recluso (traslado a hospital, juzgado, etc.)")
    public ResponseEntity<InmateDto> registerTemporaryEgress(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody TemporaryEgressDto dto,
            @Parameter(description = "Nombre del oficial que registra la salida") @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(inmateService.registerTemporaryEgress(id, dto, username));
    }

    @PostMapping("/{id}/temporary-return")
    @Operation(summary = "Retorno temporal", description = "Registra el retorno de un recluso después de una salida temporal")
    public ResponseEntity<InmateDto> registerTemporaryReturn(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody TemporaryReturnDto dto,
            @Parameter(description = "Nombre del oficial que registra el retorno") @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(inmateService.registerTemporaryReturn(id, dto, username));
    }

    @PostMapping("/{id}/relocate-emergency")
    @Operation(summary = "Reubicación de emergencia", description = "Reubica a un recluso en otra celda por razones de emergencia. Solo disponible para Supervisores.")
    public ResponseEntity<InmateDto> relocateEmergency(
            @Parameter(description = "UUID del recluso") @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @Parameter(description = "Nombre del supervisor que autoriza") @RequestHeader(value = "X-User-Name", defaultValue = "Supervisor") String username,
            @Parameter(description = "Rol del usuario (debe ser Supervisor)") @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        if (!"Supervisor".equalsIgnoreCase(role)) {
            throw new RuntimeException("Acceso denegado: Solo el rol de Supervisor está autorizado para esta operación.");
        }
        UUID targetCellId = UUID.fromString(body.get("targetCellId"));
        return ResponseEntity.ok(inmateService.relocateEmergencyInmate(id, targetCellId, username));
    }
}
