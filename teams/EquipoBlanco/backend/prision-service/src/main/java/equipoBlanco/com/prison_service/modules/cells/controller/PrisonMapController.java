package equipoBlanco.com.prison_service.modules.cells.controller;

import equipoBlanco.com.prison_service.modules.cells.dto.CellPositionDto;
import equipoBlanco.com.prison_service.modules.cells.dto.MapWithPositionsDto;
import equipoBlanco.com.prison_service.modules.cells.dto.PrisonMapDto;
import equipoBlanco.com.prison_service.modules.cells.service.CellPositionService;
import equipoBlanco.com.prison_service.modules.cells.service.PrisonMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Mapas (Prison Maps)", description = "Gestión de mapas del penal por piso: imagen de fondo y posiciones de celdas")
public class PrisonMapController {

    private final PrisonMapService prisonMapService;
    private final CellPositionService cellPositionService;

    @GetMapping("/{floorNumber}")
    @Operation(summary = "Obtener mapa por piso", description = "Obtiene los datos del mapa (imagen de fondo) de un piso específico")
    public ResponseEntity<PrisonMapDto> getMap(
            @Parameter(description = "Número de piso") @PathVariable Integer floorNumber) {
        PrisonMapDto map = prisonMapService.getMap(floorNumber);
        if (map == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(map);
    }

    @PutMapping(value = "/{floorNumber}", consumes = "application/json")
    @Operation(summary = "Guardar/actualizar mapa", description = "Guarda o actualiza la imagen de fondo de un piso")
    public ResponseEntity<PrisonMapDto> saveMap(
            @Parameter(description = "Número de piso") @PathVariable Integer floorNumber,
            @RequestBody PrisonMapDto dto) {
        return ResponseEntity.ok(prisonMapService.saveMap(floorNumber, dto.getBackgroundImage()));
    }

    @DeleteMapping("/{floorNumber}")
    @Operation(summary = "Eliminar mapa", description = "Elimina el mapa de un piso específico")
    public ResponseEntity<Void> deleteMap(
            @Parameter(description = "Número de piso") @PathVariable Integer floorNumber) {
        prisonMapService.deleteMap(floorNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{floorNumber}/positions")
    @Operation(summary = "Posiciones de celdas por piso", description = "Obtiene todas las posiciones de celdas en un piso específico")
    public ResponseEntity<List<CellPositionDto>> getPositions(
            @Parameter(description = "Número de piso") @PathVariable Integer floorNumber) {
        return ResponseEntity.ok(cellPositionService.getPositionsByFloor(floorNumber));
    }

    @PostMapping(value = "/{floorNumber}/positions", consumes = "application/json")
    @Operation(summary = "Guardar posiciones de celdas", description = "Guarda las posiciones de las celdas en un piso específico")
    public ResponseEntity<List<CellPositionDto>> savePositions(
            @Parameter(description = "Número de piso") @PathVariable Integer floorNumber,
            @RequestBody List<CellPositionDto> positions) {
        return ResponseEntity.ok(cellPositionService.savePositions(floorNumber, positions));
    }

    @DeleteMapping("/{floorNumber}/positions")
    @Operation(summary = "Eliminar todas las posiciones", description = "Elimina todas las posiciones de celdas de un piso")
    public ResponseEntity<Void> deleteAllPositions(
            @Parameter(description = "Número de piso") @PathVariable Integer floorNumber) {
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
