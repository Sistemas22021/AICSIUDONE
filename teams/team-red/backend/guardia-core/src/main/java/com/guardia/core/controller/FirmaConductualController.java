package com.guardia.core.controller;

import com.guardia.core.dto.request.ActualizarFirmaConductualRequest;
import com.guardia.core.dto.request.RegistrarFirmaConductualRequest;
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
@RequestMapping("/api/firma-conductual")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
/**
 * Controlador para el registro y consulta de la Firma Conductual de un expediente.
 * Ruta principal: /api/firma-conductual
 */
public class FirmaConductualController {

    private final FirmaConductualService firmaConductualService;

    @PostMapping
    public ResponseEntity<ApiResponse<FirmaConductualResponse>> registrar(
            @Valid @RequestBody RegistrarFirmaConductualRequest request) {

        FirmaConductualResponse response =
                firmaConductualService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "Firma conductual registrada correctamente.",
                        response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FirmaConductualResponse>> editar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarFirmaConductualRequest request) {

        FirmaConductualResponse response =
                firmaConductualService.editar(id, request);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Firma conductual actualizada correctamente.",
                        response));
    }

    @GetMapping("/expediente/{expedienteId}")
    public ResponseEntity<ApiResponse<FirmaConductualResponse>> obtenerActual(
            @PathVariable Long expedienteId) {

        FirmaConductualResponse response =
                firmaConductualService.obtenerActual(expedienteId);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Firma conductual obtenida correctamente.",
                        response));
    }

    @GetMapping("/expediente/{expedienteId}/historial")
    public ResponseEntity<ApiResponse<List<FirmaConductualResponse>>> historial(
            @PathVariable Long expedienteId) {

        List<FirmaConductualResponse> response =
                firmaConductualService.obtenerHistorial(expedienteId);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Historial obtenido correctamente.",
                        response));
    }
}