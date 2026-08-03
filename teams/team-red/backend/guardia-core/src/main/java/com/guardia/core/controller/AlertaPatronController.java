package com.guardia.core.controller;

import com.guardia.core.dto.request.DescartarAlertaRequest;
import com.guardia.core.dto.request.RevisarAlertaRequest;
import com.guardia.core.dto.response.AlertaPatronResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.AlertaPatronService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints de alertas internas de patrón de MO: panel del Guardia,
 * bandeja del Investigador, y acciones de revisar/descartar.
 */
@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
public class AlertaPatronController {

    private final AlertaPatronService alertaPatronService;

    /** Panel del Guardia: todas las alertas del sistema, pendientes primero */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertaPatronResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(alertaPatronService.listarTodas()));
    }

    /** Bandeja de notificaciones del Investigador asignado a los expedientes relacionados */
    @GetMapping("/investigador/{investigadorId}")
    public ResponseEntity<ApiResponse<List<AlertaPatronResponse>>> listarPorInvestigador(
            @PathVariable UUID investigadorId) {
        return ResponseEntity.ok(ApiResponse.ok(alertaPatronService.listarPorInvestigador(investigadorId)));
    }

    /** Marca la alerta como revisada; queda registrado el usuario y el timestamp */
    @PatchMapping("/{alertaId}/revisar")
    public ResponseEntity<ApiResponse<AlertaPatronResponse>> marcarRevisada(
            @PathVariable Long alertaId,
            @Valid @RequestBody RevisarAlertaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Alerta marcada como revisada.",
                alertaPatronService.marcarRevisada(alertaId, request)));
    }

    /** Marca la alerta como descartada; queda registrado el usuario y el timestamp */
    @PatchMapping("/{alertaId}/descartar")
    public ResponseEntity<ApiResponse<AlertaPatronResponse>> marcarDescartada(
            @PathVariable Long alertaId,
            @Valid @RequestBody DescartarAlertaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Alerta descartada.",
                alertaPatronService.marcarDescartada(alertaId, request)));
    }
}