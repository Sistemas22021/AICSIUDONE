package com.guardia.core.controller;

import com.guardia.core.dto.request.TipoDelitoRequest;
import com.guardia.core.dto.response.TipoDelitoResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.TipoDelitoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-delito")
@RequiredArgsConstructor
/**
 * Controlador REST para gestionar los tipos de delito.
 * Exposa endpoints CRUD y consultas relacionadas con tipos de delito.
 * Rutas principales: /api/v1/tipos-delito
 */
public class TipoDelitoController {

    private final TipoDelitoService tipoDelitoService;

    @PostMapping
    public ResponseEntity<ApiResponse<TipoDelitoResponse>> crear(@Valid @RequestBody TipoDelitoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tipo de delito creado.", tipoDelitoService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoDelitoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tipoDelitoService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoDelitoResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(tipoDelitoService.obtenerTodos()));
    }

    @GetMapping("/requieren-subtipo")
    public ResponseEntity<ApiResponse<List<TipoDelitoResponse>>> obtenerQueRequierenSubtipo() {
        return ResponseEntity.ok(ApiResponse.ok(tipoDelitoService.obtenerQueRequierenSubtipo()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoDelitoResponse>> actualizar(@PathVariable Long id,
                                                                       @Valid @RequestBody TipoDelitoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Tipo de delito actualizado.", tipoDelitoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        tipoDelitoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Tipo de delito eliminado.", null));
    }
}
