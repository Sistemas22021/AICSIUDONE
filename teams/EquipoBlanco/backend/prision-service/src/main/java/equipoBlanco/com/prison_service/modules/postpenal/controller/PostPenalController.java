package equipoBlanco.com.prison_service.modules.postpenal.controller;

import equipoBlanco.com.prison_service.modules.postpenal.dto.AssignOfficerDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpProfileDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpedienteDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.OficialCargaDto;
import equipoBlanco.com.prison_service.modules.postpenal.service.PostPenalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post-penal")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Post-Penitenciario (Expedientes)", description = "Gestión de expedientes de seguimiento post-penitenciario y asignación de oficiales")
public class PostPenalController {

    private final PostPenalService postPenalService;

    @GetMapping("/expedientes")
    @Operation(summary = "Listar expedientes", description = "Obtiene todos los expedientes de seguimiento post-penitenciario")
    public ResponseEntity<List<ExpedienteDto>> getAllExpedientes() {
        return ResponseEntity.ok(postPenalService.getAllExpedientes());
    }

    @GetMapping("/expedientes/unassigned")
    @Operation(summary = "Expedientes sin oficial asignado", description = "Obtiene los expedientes que aún no tienen un oficial de carga asignado")
    public ResponseEntity<List<ExpedienteDto>> getUnassigned() {
        return ResponseEntity.ok(postPenalService.getUnassignedExpedientes());
    }

    @GetMapping("/oficiales/carga")
    @Operation(summary = "Oficiales con carga de trabajo", description = "Obtiene los oficiales de seguimiento con su carga actual de expedientes asignados")
    public ResponseEntity<List<OficialCargaDto>> getOficialesCarga() {
        return ResponseEntity.ok(postPenalService.getOficialesConCarga());
    }

    @PostMapping("/expedientes/{id}/assign")
    @Operation(summary = "Asignar oficial a expediente", description = "Asigna un oficial de seguimiento a un expediente post-penitenciario")
    public ResponseEntity<ExpedienteDto> assignOfficer(
            @Parameter(description = "UUID del expediente") @PathVariable UUID id,
            @RequestBody AssignOfficerDto dto) {
        return ResponseEntity.ok(postPenalService.assignOfficer(id, dto));
    }

    @PutMapping("/expedientes/{id}/profile")
    @Operation(summary = "Completar perfil del expediente", description = "Actualiza los datos del perfil de un expediente post-penitenciario (dirección, teléfono, etc.)")
    public ResponseEntity<ExpedienteDto> completeProfile(
            @Parameter(description = "UUID del expediente") @PathVariable UUID id,
            @RequestBody ExpProfileDto dto) {
        return ResponseEntity.ok(postPenalService.completeProfile(id, dto));
    }

    @GetMapping("/expedientes/inmate/{inmateId}")
    @Operation(summary = "Expediente por recluso", description = "Obtiene el expediente post-penitenciario asociado a un recluso específico")
    public ResponseEntity<ExpedienteDto> getByInmateId(
            @Parameter(description = "UUID del recluso") @PathVariable UUID inmateId) {
        return ResponseEntity.ok(postPenalService.getByInmateId(inmateId));
    }
}
