package equipoBlanco.com.prison_service.modules.cells.controller;

import equipoBlanco.com.prison_service.modules.cells.dto.CellDto;
import equipoBlanco.com.prison_service.modules.cells.service.CellPositionService;
import equipoBlanco.com.prison_service.modules.cells.service.CellService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cells")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CellController {

    private final CellService cellService;
    private final CellPositionService cellPositionService;

    @GetMapping
    public ResponseEntity<List<CellDto>> getAll() {
        return ResponseEntity.ok(cellService.getAllCells());
    }

    @GetMapping("/placed")
    public ResponseEntity<List<CellDto>> getPlaced() {
        return ResponseEntity.ok(cellService.getPlacedCells());
    }

    @PostMapping
    public ResponseEntity<CellDto> create(@Valid @RequestBody CellDto dto) {
        return ResponseEntity.ok(cellService.createCell(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CellDto> update(@PathVariable UUID id, @Valid @RequestBody CellDto dto) {
        return ResponseEntity.ok(cellService.updateCell(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cellService.deleteCell(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cellId}/assign/{inmateId}")
    public ResponseEntity<CellDto> assign(
            @PathVariable UUID cellId,
            @PathVariable UUID inmateId,
            @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String assignedBy) {
        return ResponseEntity.ok(cellService.assignInmate(cellId, inmateId, assignedBy));
    }

    @DeleteMapping("/{cellId}/position")
    public ResponseEntity<Void> removePosition(@PathVariable UUID cellId) {
        cellPositionService.deleteByCellId(cellId);
        return ResponseEntity.noContent().build();
    }
}
