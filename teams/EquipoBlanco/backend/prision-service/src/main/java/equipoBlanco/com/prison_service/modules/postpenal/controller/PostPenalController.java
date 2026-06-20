package equipoBlanco.com.prison_service.modules.postpenal.controller;

import equipoBlanco.com.prison_service.modules.postpenal.dto.AssignOfficerDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpProfileDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpedienteDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.OficialCargaDto;
import equipoBlanco.com.prison_service.modules.postpenal.service.PostPenalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post-penal")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostPenalController {

    private final PostPenalService postPenalService;

    @GetMapping("/expedientes")
    public ResponseEntity<List<ExpedienteDto>> getAllExpedientes() {
        return ResponseEntity.ok(postPenalService.getAllExpedientes());
    }

    @GetMapping("/expedientes/unassigned")
    public ResponseEntity<List<ExpedienteDto>> getUnassigned() {
        return ResponseEntity.ok(postPenalService.getUnassignedExpedientes());
    }

    @GetMapping("/oficiales/carga")
    public ResponseEntity<List<OficialCargaDto>> getOficialesCarga() {
        return ResponseEntity.ok(postPenalService.getOficialesConCarga());
    }

    @PostMapping("/expedientes/{id}/assign")
    public ResponseEntity<ExpedienteDto> assignOfficer(@PathVariable UUID id, @RequestBody AssignOfficerDto dto) {
        return ResponseEntity.ok(postPenalService.assignOfficer(id, dto));
    }

    @PutMapping("/expedientes/{id}/profile")
    public ResponseEntity<ExpedienteDto> completeProfile(@PathVariable UUID id, @RequestBody ExpProfileDto dto) {
        return ResponseEntity.ok(postPenalService.completeProfile(id, dto));
    }
}
