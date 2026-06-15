package com.guardia.core.controller;

import com.guardia.core.dto.request.SubtipoDelitoRequest;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.SubtipoDelitoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subtipos-delito")
@RequiredArgsConstructor
/**
 * Controlador REST para gestionar subtipos de delito.
 * Permite crear, actualizar, listar y validar correspondencia con un tipo de delito.
 * Rutas principales: /api/v1/subtipos-delito
 */
public class SubtipoDelitoController {

    private final SubtipoDelitoService subtipoDelitoService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubtipoDelitoResponse>> crear(@Valid @RequestBody SubtipoDelitoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Subtipo de delito creado.", subtipoDelitoService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubtipoDelitoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(subtipoDelitoService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubtipoDelitoResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(subtipoDelitoService.obtenerTodos()));
    }

    @GetMapping("/por-tipo/{tipoDelitoId}")
    public ResponseEntity<ApiResponse<List<SubtipoDelitoResponse>>> obtenerPorTipo(@PathVariable Long tipoDelitoId) {
        return ResponseEntity.ok(ApiResponse.ok(subtipoDelitoService.obtenerPorTipoDelito(tipoDelitoId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubtipoDelitoResponse>> actualizar(@PathVariable Long id,
                                                                          @Valid @RequestBody SubtipoDelitoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Subtipo actualizado.", subtipoDelitoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        subtipoDelitoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Subtipo eliminado.", null));
    }

    @GetMapping("/{subtipoId}/validar-tipo/{tipoId}")
    public ResponseEntity<ApiResponse<Boolean>> validarCorrespondencia(@PathVariable Long subtipoId,
                                                                        @PathVariable Long tipoId) {
        return ResponseEntity.ok(ApiResponse.ok(subtipoDelitoService.validarCorrespondencia(subtipoId, tipoId)));
    }
}
