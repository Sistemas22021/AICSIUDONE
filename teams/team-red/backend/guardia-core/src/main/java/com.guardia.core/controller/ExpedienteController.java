package com.guardia.core.controller;

import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.ExpedienteService;
import com.guardia.core.dto.response.VerificacionHashResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/expedientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
/**
 * Controlador central para la gestión de expedientes.
 * Maneja registro, sellado y verificación de integridad (hash) de expedientes.
 * Ruta principal: /api/expedientes
 */
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    @PostMapping(value = "/registrar", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ExpedienteResponse>> registrarExpediente(@Valid @RequestBody ExpedienteRequest request) {
        ExpedienteResponse nuevoExpediente = expedienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Expediente registrado.", nuevoExpediente));
    }

    @PatchMapping("/{id}/sellar")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> sellar(
        @PathVariable Long id,
        @RequestParam Long agenteSelladorId) {
        return ResponseEntity.ok(ApiResponse.ok("Expediente sellado.",
            expedienteService.sellar(id, agenteSelladorId)));
    }

    @GetMapping("/{id}/verificar-integridad")
    public ResponseEntity<ApiResponse<VerificacionHashResponse>> verificarIntegridad(
        @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Verificación completada.",
            expedienteService.verificarIntegridad(id)));
    }
}