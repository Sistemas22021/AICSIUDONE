package com.guardia.core.controller;

import com.guardia.core.dto.response.FirmaConductualResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.FirmaConductualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/firmas-conductuales")
@RequiredArgsConstructor
/**
 * Búsqueda de firmas conductuales por texto plano, independiente de un
 * expediente concreto (alimenta el componente de detección de patrones).
 */
public class FirmaConductualBusquedaController {

    private final FirmaConductualService firmaConductualService;

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<FirmaConductualResponse>>> buscar(
            @RequestParam String texto) {
        return ResponseEntity.ok(ApiResponse.ok(
                firmaConductualService.buscarPorTexto(texto)));
    }
}
