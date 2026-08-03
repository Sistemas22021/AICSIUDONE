package com.guardia.core.controller;

import com.guardia.core.dto.request.LocalizacionRequest;
import com.guardia.core.dto.response.LocalizacionResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.LocalizacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/localizaciones")
@RequiredArgsConstructor
public class LocalizacionController {

    private final LocalizacionService localizacionService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocalizacionResponse>> crear(@Valid @RequestBody LocalizacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Localización creada.", localizacionService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocalizacionResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(localizacionService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocalizacionResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(localizacionService.obtenerTodos()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocalizacionResponse>> actualizar(@PathVariable Long id,
                                                                         @Valid @RequestBody LocalizacionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Localización actualizada.", localizacionService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        localizacionService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Localización eliminada.", null));
    }

    @PatchMapping("/{id}/gps")
    public ResponseEntity<ApiResponse<LocalizacionResponse>> registrarGPS(@PathVariable Long id,
                                                                           @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(ApiResponse.ok("GPS registrado.",
                localizacionService.registrarGPS(id, body.get("latitud"), body.get("longitud"))));
    }

    @PatchMapping("/{id}/direccion-manual")
    public ResponseEntity<ApiResponse<LocalizacionResponse>> registrarDireccionManual(@PathVariable Long id,
                                                                                       @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Dirección registrada.",
                localizacionService.registrarDireccionManual(id,
                        body.get("municipio"), body.get("sector"),
                        body.get("direccion"), body.get("referencia"))));
    }

    @GetMapping("/{id}/validar")
    public ResponseEntity<ApiResponse<Boolean>> validarUbicacion(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(localizacionService.validarUbicacion(id)));
    }
}
