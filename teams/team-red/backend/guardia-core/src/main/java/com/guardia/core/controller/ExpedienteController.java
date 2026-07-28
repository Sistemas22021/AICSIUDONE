package com.guardia.core.controller;

import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.request.ExpedienteFiltroRequest;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.dto.response.ExpedienteActivoResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.ExpedienteService;
import com.guardia.core.service.DeteccionModusOperandiService;
import com.guardia.core.dto.response.VerificacionHashResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controlador central para la gestión de expedientes.
 * Maneja registro, sellado y verificación de integridad (hash) de expedientes.
 * Ruta principal: /api/expedientes
 */
@RestController
@RequestMapping("/api/v1/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;
    private final DeteccionModusOperandiService deteccionModusOperandiService;

    @PostMapping(value = "/registrar", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ExpedienteResponse>> registrarExpediente(@Valid @RequestBody ExpedienteRequest request) {
        ExpedienteResponse nuevoExpediente = expedienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Expediente registrado.", nuevoExpediente));
    }

    @PatchMapping("/{id}/sellar")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> sellar(
        @PathVariable Long id,
        @RequestParam UUID agenteSelladorId) {
        return ResponseEntity.ok(ApiResponse.ok("Expediente sellado.",
            expedienteService.sellar(id, agenteSelladorId)));
    }

    @GetMapping("/{id}/verificar-integridad")
    public ResponseEntity<ApiResponse<VerificacionHashResponse>> verificarIntegridad(
        @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Verificación completada.",
            expedienteService.verificarIntegridad(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpedienteActivoResponse>>> listar(
            @RequestParam(required = false) String estatus,
            @RequestParam(required = false) String sort) {

        List<ExpedienteActivoResponse> expedientes = expedienteService.obtenerParaPanel(estatus, sort);
        return ResponseEntity.ok(ApiResponse.ok("Expedientes obtenidos.", expedientes));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ExpedienteActivoResponse>>> buscarConFiltros(
            @RequestParam(required = false) List<String> tiposDelito,
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) String colonia,
            @RequestParam(required = false) Double latitud,
            @RequestParam(required = false) Double longitud,
            @RequestParam(required = false) Double radioKm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String sort) {

        ExpedienteFiltroRequest filtro = new ExpedienteFiltroRequest(
                tiposDelito, municipio, colonia, latitud, longitud, radioKm, fechaDesde, fechaHasta);

        List<ExpedienteActivoResponse> resultado = expedienteService.buscarConFiltros(filtro, sort);

        String mensaje = resultado.isEmpty() ? "Sin resultados." : "Búsqueda completada.";
        return ResponseEntity.ok(ApiResponse.ok(mensaje, resultado));
    }

    @PostMapping("/{id}/reanalizar-mo")
    public ResponseEntity<ApiResponse<Void>> reanalizarModusOperandi(@PathVariable Long id) {
        deteccionModusOperandiService.analizarPatrones(id);
        return ResponseEntity.accepted()
                .body(ApiResponse.ok("Reanálisis de Modus Operandi encolado.", null));
    }
}