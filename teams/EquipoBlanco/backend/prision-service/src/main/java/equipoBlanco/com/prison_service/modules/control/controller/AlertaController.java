package equipoBlanco.com.prison_service.modules.control.controller;

import equipoBlanco.com.prison_service.modules.control.dto.AlertaDto;
import equipoBlanco.com.prison_service.modules.control.dto.AtenderAlertaDto;
import equipoBlanco.com.prison_service.modules.control.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaService alertaService;

    /**
     * GET /api/v1/alertas?destinatario={username}
     * Devuelve las alertas activas del usuario logueado (para la campana de notificaciones).
     */
    @GetMapping
    public ResponseEntity<List<AlertaDto>> obtenerAlertasPorDestinatario(
            @RequestParam String destinatario) {
        return ResponseEntity.ok(alertaService.obtenerAlertasActivasPorDestinatario(destinatario));
    }

    /**
     * GET /api/v1/alertas/nivel1
     * Devuelve todas las alertas de Nivel 1 (activas e históricas) para el dashboard.
     */
    @GetMapping("/nivel1")
    public ResponseEntity<List<AlertaDto>> obtenerAlertasNivel1() {
        return ResponseEntity.ok(alertaService.obtenerAlertasNivel1());
    }

    /**
     * GET /api/v1/alertas/nivel2
     * Devuelve todas las alertas de Nivel 2 (activas e históricas) para el dashboard.
     */
    @GetMapping("/nivel2")
    public ResponseEntity<List<AlertaDto>> obtenerAlertasNivel2() {
        return ResponseEntity.ok(alertaService.obtenerAlertasNivel2());
    }

    /**
     * GET /api/v1/alertas/nivel3
     * Devuelve todas las alertas críticas de Nivel 3 para el dashboard.
     */
    @GetMapping("/nivel3")
    public ResponseEntity<List<AlertaDto>> obtenerAlertasNivel3() {
        return ResponseEntity.ok(alertaService.obtenerAlertasNivel3());
    }

    /**
     * PUT /api/v1/alertas/{id}/atender
     * Marca la alerta como atendida y registra la observación opcional del oficial.
     */
    @PutMapping("/{id}/atender")
    public ResponseEntity<AlertaDto> atenderAlerta(
            @PathVariable UUID id,
            @RequestBody AtenderAlertaDto dto) {
        return ResponseEntity.ok(alertaService.atenderAlerta(id, dto));
    }
}
