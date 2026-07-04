package equipoBlanco.com.prison_service.modules.postpenal.controller;

import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioCreateDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioUpdateDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CumplimientoDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.IncumplimientoDto;
import equipoBlanco.com.prison_service.modules.postpenal.service.CalendarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calendario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Calendario (Presentaciones)", description = "Gestión del calendario de presentaciones de seguimiento post-penitenciario")
public class CalendarioController {

    private final CalendarioService calendarioService;

    @PostMapping("/{expedienteId}")
    @Operation(summary = "Generar calendario", description = "Genera las fechas de presentación para un expediente de seguimiento post-penitenciario")
    public ResponseEntity<List<CalendarioDto>> generarCalendario(
            @Parameter(description = "UUID del expediente") @PathVariable UUID expedienteId,
            @RequestBody CalendarioCreateDto dto) {
        return ResponseEntity.ok(calendarioService.generarCalendario(expedienteId, dto));
    }

    @GetMapping("/{expedienteId}")
    @Operation(summary = "Obtener calendario por expediente", description = "Obtiene todas las fechas de presentación de un expediente")
    public ResponseEntity<List<CalendarioDto>> obtenerCalendario(
            @Parameter(description = "UUID del expediente") @PathVariable UUID expedienteId) {
        return ResponseEntity.ok(calendarioService.obtenerCalendarioPorExpediente(expedienteId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar fecha de presentación", description = "Actualiza una fecha de presentación específica")
    public ResponseEntity<CalendarioDto> actualizarFecha(
            @Parameter(description = "UUID de la fecha de presentación") @PathVariable UUID id,
            @RequestBody CalendarioUpdateDto dto) {
        return ResponseEntity.ok(calendarioService.actualizarFecha(id, dto));
    }

    @GetMapping("/pendientes/hoy")
    @Operation(summary = "Presentaciones pendientes hoy", description = "Obtiene las presentaciones programadas para hoy, opcionalmente filtradas por cédula del oficial")
    public ResponseEntity<List<CalendarioDto>> obtenerPendientesHoy(
            @Parameter(description = "Cédula del oficial (opcional)") @RequestParam(required = false) String oficialCedula) {
        return ResponseEntity.ok(calendarioService.obtenerPendientesHoy(oficialCedula));
    }

    @PutMapping("/{id}/registrar")
    @Operation(summary = "Registrar cumplimiento", description = "Registra que una presentación fue cumplida (asistió)")
    public ResponseEntity<CalendarioDto> registrarCumplimiento(
            @Parameter(description = "UUID de la fecha de presentación") @PathVariable UUID id,
            @RequestBody CumplimientoDto dto) {
        return ResponseEntity.ok(calendarioService.registrarCumplimiento(id, dto));
    }

    @PostMapping("/{id}/incumplimiento")
    @Operation(summary = "Registrar incumplimiento", description = "Registra que una presentación no fue cumplida (no asistió)")
    public ResponseEntity<CalendarioDto> registrarIncumplimiento(
            @Parameter(description = "UUID de la fecha de presentación") @PathVariable UUID id,
            @RequestBody IncumplimientoDto dto) {
        return ResponseEntity.ok(calendarioService.registrarIncumplimiento(id, dto));
    }

    @PostMapping("/procesar-vencidas")
    @Operation(summary = "Procesar presentaciones vencidas manualmente", description = "Ejecuta de manera inmediata la detección automática de presentaciones vencidas (proceso nocturno)")
    public ResponseEntity<Void> procesarPresentacionesVencidas() {
        calendarioService.procesarPresentacionesVencidas();
        return ResponseEntity.ok().build();
    }
}
