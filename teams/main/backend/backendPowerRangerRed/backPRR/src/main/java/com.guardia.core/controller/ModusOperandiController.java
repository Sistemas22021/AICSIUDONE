package com.guardia.core.controller;

import com.guardia.core.dto.request.ModusOperandiRequest;
import com.guardia.core.dto.response.ModusOperandiResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.ModusOperandiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/modus-operandi")
@RequiredArgsConstructor
public class ModusOperandiController {

    private final ModusOperandiService modusOperandiService;

    @PostMapping
    public ResponseEntity<ApiResponse<ModusOperandiResponse>> crear(@Valid @RequestBody ModusOperandiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Modus operandi creado.", modusOperandiService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModusOperandiResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(modusOperandiService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModusOperandiResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(modusOperandiService.obtenerTodos()));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ModusOperandiResponse>>> buscarPorPatron(@RequestParam String patron) {
        return ResponseEntity.ok(ApiResponse.ok(modusOperandiService.buscarPorPatron(patron)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModusOperandiResponse>> actualizar(@PathVariable Long id,
                                                                          @Valid @RequestBody ModusOperandiRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Modus operandi actualizado.",
                modusOperandiService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        modusOperandiService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Modus operandi eliminado.", null));
    }

    @PostMapping("/{modusId}/expedientes/{expedienteId}")
    public ResponseEntity<ApiResponse<ModusOperandiResponse>> vincularExpediente(@PathVariable Long modusId,
                                                                                  @PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok("Expediente vinculado.",
                modusOperandiService.vincularExpediente(modusId, expedienteId)));
    }

    @DeleteMapping("/{modusId}/expedientes/{expedienteId}")
    public ResponseEntity<ApiResponse<ModusOperandiResponse>> desvincularExpediente(@PathVariable Long modusId,
                                                                                     @PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok("Expediente desvinculado.",
                modusOperandiService.desvincularExpediente(modusId, expedienteId)));
    }

    @PatchMapping("/{id}/patron")
    public ResponseEntity<ApiResponse<ModusOperandiResponse>> agregarPatron(@PathVariable Long id,
                                                                             @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Patrón agregado.",
                modusOperandiService.agregarPatron(id, body.get("patron"))));
    }

    @GetMapping("/{modusId}/comparar")
    public ResponseEntity<ApiResponse<Double>> compararExpedientes(@PathVariable Long modusId,
                                                                    @RequestParam Long expedienteAId,
                                                                    @RequestParam Long expedienteBId) {
        return ResponseEntity.ok(ApiResponse.ok(
                modusOperandiService.compararExpedientes(modusId, expedienteAId, expedienteBId)));
    }
}
