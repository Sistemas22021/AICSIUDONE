package com.guardia.core.controller;

import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.model.enums.EstadoExpediente;
import com.guardia.core.service.ExpedienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpedienteResponse>> crear(@Valid @RequestBody ExpedienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Expediente creado.", expedienteService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(expedienteService.obtenerPorId(id)));
    }

    @GetMapping("/folio/{folio}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> obtenerPorFolio(@PathVariable String folio) {
        return ResponseEntity.ok(ApiResponse.ok(expedienteService.obtenerPorFolio(folio)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpedienteResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(expedienteService.obtenerTodos()));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<List<ExpedienteResponse>>> obtenerPorEstado(@PathVariable EstadoExpediente estado) {
        return ResponseEntity.ok(ApiResponse.ok(expedienteService.obtenerPorEstado(estado)));
    }

    @GetMapping("/creador/{usuarioId}")
    public ResponseEntity<ApiResponse<List<ExpedienteResponse>>> obtenerPorCreador(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.ok(expedienteService.obtenerPorCreador(usuarioId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> actualizar(@PathVariable Long id,
                                                                       @Valid @RequestBody ExpedienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Expediente actualizado.", expedienteService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        expedienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Expediente eliminado.", null));
    }

    // ── Endpoints de negocio del diagrama ──────────────────────────────────────

    @PatchMapping("/{id}/sellar/{agenteSelladorId}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> sellar(@PathVariable Long id,
                                                                   @PathVariable Long agenteSelladorId) {
        return ResponseEntity.ok(ApiResponse.ok("Expediente sellado.", expedienteService.sellar(id, agenteSelladorId)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> cambiarEstado(@PathVariable Long id,
                                                                          @RequestBody Map<String, String> body) {
        EstadoExpediente nuevoEstado = EstadoExpediente.valueOf(body.get("estado"));
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado.",
                expedienteService.cambiarEstado(id, nuevoEstado)));
    }

    @PatchMapping("/{id}/asignar-investigador/{investigadorId}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> asignarInvestigador(@PathVariable Long id,
                                                                                @PathVariable Long investigadorId) {
        return ResponseEntity.ok(ApiResponse.ok("Investigador asignado.",
                expedienteService.asignarInvestigador(id, investigadorId)));
    }

    @PatchMapping("/{id}/vincular-escena/{escenaId}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> vincularEscena(@PathVariable Long id,
                                                                           @PathVariable Long escenaId) {
        return ResponseEntity.ok(ApiResponse.ok("Escena vinculada.",
                expedienteService.vincularEscena(id, escenaId)));
    }

    @PatchMapping("/{id}/fecha-hecho")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> asignarFechaHecho(@PathVariable Long id,
                                                                              @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Fecha asignada.",
                expedienteService.asignarFechaHecho(id, body.get("fecha"))));
    }

    @GetMapping("/{id}/validar-datos")
    public ResponseEntity<ApiResponse<Boolean>> validarDatos(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(expedienteService.validarDatos(id)));
    }
}
