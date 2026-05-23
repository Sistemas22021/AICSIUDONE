package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.InmateDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.service.InmateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inmates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InmateController {

    private final InmateService inmateService;

    @GetMapping("/check-cedula/{cedula}")
    public ResponseEntity<Map<String, Boolean>> checkCedula(@PathVariable String cedula) {
        boolean exists = inmateService.cedulaHasActiveRecord(cedula);
        return ResponseEntity.ok(Map.of("hasActiveRecord", exists));
    }

    @PostMapping
    public ResponseEntity<InmateDto> register(@RequestBody InmateDto dto) {
        return ResponseEntity.ok(inmateService.register(dto));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<InmateDto>> getUnassigned() {
        return ResponseEntity.ok(inmateService.getByStatus(InmateStatus.ACTIVO_SIN_CELDA));
    }
}
