package com.guardia.core.controller;

import com.guardia.core.dto.request.AprobarPropuestaMoRequest;
import com.guardia.core.dto.request.CorregirPropuestaMoRequest;
import com.guardia.core.dto.request.RechazarPropuestaMoRequest;
import com.guardia.core.dto.response.PropuestaModusOperandiResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.PropuestaModusOperandiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expedientes/{expedienteId}/modus-operandi")
@RequiredArgsConstructor
/**
 * Endpoints de lectura de la propuesta de MO generada por IA (HU2) y de las
 * acciones de validación experta (HU3): aprobar, corregir, rechazar.
 */
public class PropuestaModusOperandiController {

    private final PropuestaModusOperandiService propuestaModusOperandiService;

    @GetMapping
    public ResponseEntity<ApiResponse<PropuestaModusOperandiResponse>> obtenerVigente(
            @PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok(
                propuestaModusOperandiService.obtenerVigentePorExpediente(expedienteId)));
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<PropuestaModusOperandiResponse>>> obtenerHistorial(
            @PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok(
                propuestaModusOperandiService.historialPorExpediente(expedienteId)));
    }

    @PostMapping("/{propuestaId}/aprobar")
    public ResponseEntity<ApiResponse<PropuestaModusOperandiResponse>> aprobar(
            @PathVariable Long expedienteId,
            @PathVariable Long propuestaId,
            @Valid @RequestBody AprobarPropuestaMoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Propuesta de MO aprobada.",
                propuestaModusOperandiService.aprobar(propuestaId, request)));
    }

    @PostMapping("/{propuestaId}/corregir")
    public ResponseEntity<ApiResponse<PropuestaModusOperandiResponse>> corregir(
            @PathVariable Long expedienteId,
            @PathVariable Long propuestaId,
            @Valid @RequestBody CorregirPropuestaMoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Propuesta de MO corregida.",
                propuestaModusOperandiService.corregir(propuestaId, request)));
    }

    @PostMapping("/{propuestaId}/rechazar")
    public ResponseEntity<ApiResponse<PropuestaModusOperandiResponse>> rechazar(
            @PathVariable Long expedienteId,
            @PathVariable Long propuestaId,
            @Valid @RequestBody RechazarPropuestaMoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Propuesta de MO rechazada.",
                propuestaModusOperandiService.rechazar(propuestaId, request)));
    }
}