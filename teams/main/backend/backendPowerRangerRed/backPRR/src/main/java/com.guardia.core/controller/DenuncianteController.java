package com.guardia.core.controller;

import com.guardia.core.dto.request.DenuncianteRequest;
import com.guardia.core.dto.response.DenuncianteResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.DenuncianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/denunciantes")
@RequiredArgsConstructor
public class DenuncianteController {

    private final DenuncianteService denuncianteService;

    @PostMapping
    public ResponseEntity<ApiResponse<DenuncianteResponse>> crear(@Valid @RequestBody DenuncianteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Denunciante creado.", denuncianteService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DenuncianteResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(denuncianteService.obtenerPorId(id)));
    }

    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<ApiResponse<DenuncianteResponse>> obtenerPorIdentificacion(@PathVariable String identificacion) {
        return ResponseEntity.ok(ApiResponse.ok(denuncianteService.obtenerPorIdentificacion(identificacion)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DenuncianteResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(denuncianteService.obtenerTodos()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DenuncianteResponse>> actualizar(@PathVariable Long id,
                                                                        @Valid @RequestBody DenuncianteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Denunciante actualizado.", denuncianteService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        denuncianteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Denunciante eliminado.", null));
    }

    @PatchMapping("/{id}/relacion")
    public ResponseEntity<ApiResponse<DenuncianteResponse>> registrarRelacion(@PathVariable Long id,
                                                                               @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Relación registrada.",
                denuncianteService.registrarRelacion(id, body.get("relacion"))));
    }
}
