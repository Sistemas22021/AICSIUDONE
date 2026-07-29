package com.guardia.core.controller;

import com.guardia.core.dto.request.FirmaConductualRequest;
import com.guardia.core.dto.response.FirmaConductualResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.FirmaConductualService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expedientes/{expedienteId}/firma-conductual")
@RequiredArgsConstructor
/**
 * Registro, edición versionada e historial de la firma conductual de un
 * expediente (HU "Registrar firma conductual del caso").
 */
public class FirmaConductualController {

    private final FirmaConductualService firmaConductualService;

    @PostMapping
    public ResponseEntity<ApiResponse<FirmaConductualResponse>> registrar(
            @PathVariable Long expedienteId,
            @Valid @RequestBody FirmaConductualRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(
                "Firma conductual registrada.",
                firmaConductualService.registrarONuevaVersion(expedienteId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FirmaConductualResponse>> obtenerVigente(
            @PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok(
                firmaConductualService.obtenerVigente(expedienteId)));
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<FirmaConductualResponse>>> historial(
            @PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok(
                firmaConductualService.historial(expedienteId)));
    }
}
