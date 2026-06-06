package com.guardia.core.controller;

import com.guardia.core.dto.request.EscenaRequest;
import com.guardia.core.dto.response.EscenaResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.EscenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/escenas")
@RequiredArgsConstructor
public class EscenaController {

    private final EscenaService escenaService;

    @PostMapping
    public ResponseEntity<ApiResponse<EscenaResponse>> crear(@Valid @RequestBody EscenaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Escena creada.", escenaService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EscenaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(escenaService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EscenaResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(escenaService.obtenerTodos()));
    }

    @GetMapping("/por-expediente/{expedienteId}")
    public ResponseEntity<ApiResponse<List<EscenaResponse>>> obtenerPorExpediente(@PathVariable Long expedienteId) {
        return ResponseEntity.ok(ApiResponse.ok(escenaService.obtenerPorExpediente(expedienteId)));
    }

    @GetMapping("/por-investigador/{usuarioId}")
    public ResponseEntity<ApiResponse<List<EscenaResponse>>> obtenerPorInvestigador(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.ok(escenaService.obtenerPorInvestigador(usuarioId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        escenaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Escena eliminada.", null));
    }

    @PatchMapping("/{id}/iniciar-checklist")
    public ResponseEntity<ApiResponse<EscenaResponse>> iniciarChecklist(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Checklist iniciado.", escenaService.iniciarChecklist(id)));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<ApiResponse<EscenaResponse>> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Escena cerrada.", escenaService.cerrar(id)));
    }

    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<ApiResponse<EscenaResponse>> bloquearEdicion(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Edición bloqueada.", escenaService.bloquearEdicion(id)));
    }

    @GetMapping("/{id}/validar-secuencia")
    public ResponseEntity<ApiResponse<Boolean>> validarSecuencia(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(escenaService.validarSecuencia(id)));
    }

    @PatchMapping("/{id}/avanzar")
    public ResponseEntity<ApiResponse<EscenaResponse>> avanzarPaso(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Paso completado.",
                        escenaService.avanzarPaso(id)
                )
        );
    }

}
