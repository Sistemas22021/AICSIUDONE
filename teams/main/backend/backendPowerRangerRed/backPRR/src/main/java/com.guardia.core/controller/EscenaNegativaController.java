package com.guardia.core.controller;

import com.guardia.core.dto.request.EscenaNegativaRequest;
import com.guardia.core.dto.response.EscenaNegativaResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.EscenaNegativaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/escenas-negativas")
@RequiredArgsConstructor
public class EscenaNegativaController {

    private final EscenaNegativaService escenaNegativaService;

    @PostMapping
    public ResponseEntity<ApiResponse<EscenaNegativaResponse>> crear(@Valid @RequestBody EscenaNegativaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Escena negativa registrada.", escenaNegativaService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EscenaNegativaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(escenaNegativaService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EscenaNegativaResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(escenaNegativaService.obtenerTodos()));
    }

    @GetMapping("/por-escena/{escenaId}")
    public ResponseEntity<ApiResponse<List<EscenaNegativaResponse>>> obtenerPorEscena(@PathVariable Long escenaId) {
        return ResponseEntity.ok(ApiResponse.ok(escenaNegativaService.obtenerPorEscena(escenaId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EscenaNegativaResponse>> actualizar(@PathVariable Long id,
                                                                           @Valid @RequestBody EscenaNegativaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Escena negativa actualizada.",
                escenaNegativaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        escenaNegativaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Escena negativa eliminada.", null));
    }

    @PatchMapping("/{id}/resultado-no-encontrado")
    public ResponseEntity<ApiResponse<EscenaNegativaResponse>> registrarResultadoNoEncontrado(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Resultado registrado.",
                escenaNegativaService.registrarResultadoNoEncontrado(id,
                        body.get("area"), body.get("observacion"))));
    }

    @PatchMapping("/{id}/observacion")
    public ResponseEntity<ApiResponse<EscenaNegativaResponse>> agregarObservacion(@PathVariable Long id,
                                                                                   @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Observación agregada.",
                escenaNegativaService.agregarObservacion(id, body.get("observacion"))));
    }

    @GetMapping("/{id}/validar")
    public ResponseEntity<ApiResponse<Boolean>> validarRegistro(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(escenaNegativaService.validarRegistro(id)));
    }
}
