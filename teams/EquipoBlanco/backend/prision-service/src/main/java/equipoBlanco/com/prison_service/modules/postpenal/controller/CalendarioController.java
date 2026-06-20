package equipoBlanco.com.prison_service.modules.postpenal.controller;

import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioCreateDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.CalendarioUpdateDto;
import equipoBlanco.com.prison_service.modules.postpenal.service.CalendarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calendario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CalendarioController {

    private final CalendarioService calendarioService;

    @PostMapping("/{expedienteId}")
    public ResponseEntity<List<CalendarioDto>> generarCalendario(
            @PathVariable UUID expedienteId,
            @RequestBody CalendarioCreateDto dto) {
        return ResponseEntity.ok(calendarioService.generarCalendario(expedienteId, dto));
    }

    @GetMapping("/{expedienteId}")
    public ResponseEntity<List<CalendarioDto>> obtenerCalendario(@PathVariable UUID expedienteId) {
        return ResponseEntity.ok(calendarioService.obtenerCalendarioPorExpediente(expedienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarioDto> actualizarFecha(
            @PathVariable UUID id,
            @RequestBody CalendarioUpdateDto dto) {
        return ResponseEntity.ok(calendarioService.actualizarFecha(id, dto));
    }

    @GetMapping("/pendientes/hoy")
    public ResponseEntity<List<CalendarioDto>> obtenerPendientesHoy(
            @RequestParam(required = false) String oficialCedula) {
        return ResponseEntity.ok(calendarioService.obtenerPendientesHoy(oficialCedula));
    }
}
