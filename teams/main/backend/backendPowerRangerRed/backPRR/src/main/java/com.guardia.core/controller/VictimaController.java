package com.guardia.core.controller;

import com.guardia.core.dto.request.VictimaRequest;
import com.guardia.core.dto.response.VictimaResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.VictimaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/victimas")
@RequiredArgsConstructor
/**
 * Controlador REST para gestionar víctimas.
 * Provee endpoints CRUD y operaciones de validación de identificación.
 * Rutas principales: /api/v1/victimas
 */
public class VictimaController {

    private final VictimaService victimaService;

    @PostMapping
    public ResponseEntity<ApiResponse<VictimaResponse>> crear(@Valid @RequestBody VictimaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Víctima registrada.", victimaService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VictimaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(victimaService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VictimaResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(victimaService.obtenerTodos()));
    }

    @GetMapping("/por-expediente/{expedienteId}")
    public ResponseEntity<ApiResponse<List<VictimaResponse>>> obtenerPorExpediente(@PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok(victimaService.obtenerPorExpediente(expedienteId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VictimaResponse>> actualizar(@PathVariable Long id,
                                                                    @Valid @RequestBody VictimaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Víctima actualizada.", victimaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        victimaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Víctima eliminada.", null));
    }

    @GetMapping("/{id}/validar-identificacion")
    public ResponseEntity<ApiResponse<Boolean>> validarIdentificacion(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(victimaService.validarIdentificacion(id)));
    }
}
