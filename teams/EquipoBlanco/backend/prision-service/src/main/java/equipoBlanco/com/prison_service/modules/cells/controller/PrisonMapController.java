package equipoBlanco.com.prison_service.modules.cells.controller;

import equipoBlanco.com.prison_service.modules.cells.dto.CellPositionDto;
import equipoBlanco.com.prison_service.modules.cells.dto.PrisonMapDto;
import equipoBlanco.com.prison_service.modules.cells.service.CellPositionService;
import equipoBlanco.com.prison_service.modules.cells.service.PrisonMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrisonMapController {

    private final PrisonMapService prisonMapService;
    private final CellPositionService cellPositionService;

    @GetMapping("/{floorNumber}")
    public ResponseEntity<PrisonMapDto> getMap(@PathVariable Integer floorNumber) {
        PrisonMapDto map = prisonMapService.getMap(floorNumber);
        if (map == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(map);
    }

    @PutMapping(value = "/{floorNumber}", consumes = "application/json")
    public ResponseEntity<PrisonMapDto> saveMap(
            @PathVariable Integer floorNumber,
            @RequestBody PrisonMapDto dto) {
        return ResponseEntity.ok(prisonMapService.saveMap(floorNumber, dto.getBackgroundImage()));
    }

    @DeleteMapping("/{floorNumber}")
    public ResponseEntity<Void> deleteMap(@PathVariable Integer floorNumber) {
        prisonMapService.deleteMap(floorNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{floorNumber}/positions")
    public ResponseEntity<List<CellPositionDto>> getPositions(@PathVariable Integer floorNumber) {
        return ResponseEntity.ok(cellPositionService.getPositionsByFloor(floorNumber));
    }

    @PostMapping(value = "/{floorNumber}/positions", consumes = "application/json")
    public ResponseEntity<List<CellPositionDto>> savePositions(
            @PathVariable Integer floorNumber,
            @RequestBody List<CellPositionDto> positions) {
        return ResponseEntity.ok(cellPositionService.savePositions(floorNumber, positions));
    }

    @DeleteMapping("/{floorNumber}/positions")
    public ResponseEntity<Void> deleteAllPositions(@PathVariable Integer floorNumber) {
        List<CellPositionDto> existing = cellPositionService.getPositionsByFloor(floorNumber);
        for (CellPositionDto pos : existing) {
            cellPositionService.deleteByCellId(UUID.fromString(pos.getCellId()));
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Map.of("error", ex.getMessage()));
    }
}
