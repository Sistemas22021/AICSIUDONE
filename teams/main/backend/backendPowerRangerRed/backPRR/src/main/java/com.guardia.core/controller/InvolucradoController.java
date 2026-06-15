package com.guardia.core.controller;

import com.guardia.core.dto.response.InvolucradoResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.service.InvolucradoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/involucrados")
@RequiredArgsConstructor
/**
 * Controlador REST para gestionar involucrados en expedientes (víctimas, testigos, sospechosos).
 * Exponer operaciones de consulta y eliminación por expediente o rol.
 * Rutas principales: /api/v1/involucrados
 */
public class InvolucradoController {

    private final InvolucradoService involucradoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvolucradoResponse>>> obtenerTodos() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        involucradoService.obtenerTodos()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvolucradoResponse>> obtenerPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        involucradoService.obtenerPorId(id)
                )
        );
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<ApiResponse<List<InvolucradoResponse>>> obtenerPorRol(
            @PathVariable TipoRol rol
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        involucradoService.obtenerPorRol(rol)
                )
        );
    }

    @GetMapping("/expediente/{expedienteId}")
    public ResponseEntity<ApiResponse<List<InvolucradoResponse>>> obtenerPorExpediente(
            @PathVariable Long expedienteId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        involucradoService.obtenerPorExpediente(expedienteId)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id
    ) {
        involucradoService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Involucrado eliminado.",
                        null
                )
        );
    }
}