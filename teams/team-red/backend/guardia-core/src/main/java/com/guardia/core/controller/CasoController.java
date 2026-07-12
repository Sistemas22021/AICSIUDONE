package com.guardia.core.controller;

import com.guardia.core.dto.request.CasoRequest;
import com.guardia.core.dto.response.CasoResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.CasoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/casos")
@RequiredArgsConstructor
public class CasoController {

    private final CasoService casoService;

    @PostMapping
    public ResponseEntity<ApiResponse<CasoResponse>> crear(@Valid @RequestBody CasoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Caso creado correctamente.", casoService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CasoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(casoService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CasoResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(casoService.obtenerTodos()));
    }
}