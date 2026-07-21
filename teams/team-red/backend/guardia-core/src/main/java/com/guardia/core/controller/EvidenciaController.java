package com.guardia.core.controller;

import com.guardia.core.dto.request.EvidenciaRequest;
import com.guardia.core.dto.response.EvidenciaResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.EvidenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evidencias")
@RequiredArgsConstructor
/**
 * Controlador REST para gestionar evidencias forenses.
 * Soporta CRUD, asignación de número, firma de levantamiento y verificación de integridad/hash.
 * Rutas principales: /api/v1/evidencias
 */
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    @PostMapping
    public ResponseEntity<ApiResponse<EvidenciaResponse>> crear(@Valid @RequestBody EvidenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Evidencia registrada.", evidenciaService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvidenciaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(evidenciaService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EvidenciaResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(evidenciaService.obtenerTodos()));
    }

    @GetMapping("/por-escena/{escenaId}")
    public ResponseEntity<ApiResponse<List<EvidenciaResponse>>> obtenerPorEscena(@PathVariable Long escenaId) {
        return ResponseEntity.ok(ApiResponse.ok(evidenciaService.obtenerPorEscena(escenaId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvidenciaResponse>> actualizar(@PathVariable Long id,
                                                                      @Valid @RequestBody EvidenciaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Evidencia actualizada.", evidenciaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        evidenciaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Evidencia eliminada.", null));
    }

    @PatchMapping("/{id}/numero")
    public ResponseEntity<ApiResponse<EvidenciaResponse>> asignarNumero(@PathVariable Long id,
                                                                         @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Número asignado.",
                evidenciaService.asignarNumero(id, body.get("numero"))));
    }

    @PatchMapping("/{id}/firmar/{investigadorId}")
    public ResponseEntity<ApiResponse<EvidenciaResponse>> firmarLevantamiento(@PathVariable Long id,
                                                                              @PathVariable UUID investigadorId) {
        return ResponseEntity.ok(ApiResponse.ok("Levantamiento firmado.",
                evidenciaService.firmarLevantamiento(id, investigadorId)));
    }

    @GetMapping("/{id}/validar-integridad")
    public ResponseEntity<ApiResponse<Boolean>> validarIntegridad(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(evidenciaService.validarIntegridad(id)));
    }

    @PostMapping("/{id}/verificar-hash")
    public ResponseEntity<ApiResponse<Boolean>> verificarHash(@PathVariable Long id) {
        boolean valido = evidenciaService.verificarHash(id);
        String mensaje = valido
                ? "Integridad verificada: el hash coincide."
                : "ALERTA: discrepancia de integridad detectada.";
        return ResponseEntity.ok(ApiResponse.ok(mensaje, valido));
    }
}
