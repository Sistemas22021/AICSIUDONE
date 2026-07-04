package equipoBlanco.com.prison_service.modules.cells.controller;

import equipoBlanco.com.prison_service.modules.cells.dto.CellDto;
import equipoBlanco.com.prison_service.modules.cells.service.CellPositionService;
import equipoBlanco.com.prison_service.modules.cells.service.CellService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Celdas (Cells)", description = "Gestión de celdas: CRUD, asignación de reclusos y posiciones en el mapa")
public class CellController {

    private final CellService cellService;
    private final CellPositionService cellPositionService;

    @GetMapping
    @Operation(summary = "Listar celdas", description = "Obtiene todas las celdas registradas en el sistema")
    public ResponseEntity<List<CellDto>> getAll() {
        return ResponseEntity.ok(cellService.getAllCells());
    }

    @GetMapping("/placed")
    @Operation(summary = "Celdas colocadas", description = "Obtiene solo las celdas que tienen una posición asignada en el mapa")
    public ResponseEntity<List<CellDto>> getPlaced() {
        return ResponseEntity.ok(cellService.getPlacedCells());
    }

    @PostMapping
    @Operation(summary = "Crear celda", description = "Crea una nueva celda con sus datos (bloque, número, capacidad, tipo)")
    public ResponseEntity<CellDto> create(@Valid @RequestBody CellDto dto) {
        return ResponseEntity.ok(cellService.createCell(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar celda", description = "Actualiza los datos de una celda existente")
    public ResponseEntity<CellDto> update(
            @Parameter(description = "UUID de la celda") @PathVariable UUID id,
            @Valid @RequestBody CellDto dto) {
        return ResponseEntity.ok(cellService.updateCell(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar celda", description = "Elimina una celda del sistema")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID de la celda") @PathVariable UUID id) {
        cellService.deleteCell(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cellId}/assign/{inmateId}")
    @Operation(summary = "Asignar recluso a celda", description = "Asigna un recluso a una celda específica")
    public ResponseEntity<CellDto> assign(
            @Parameter(description = "UUID de la celda") @PathVariable UUID cellId,
            @Parameter(description = "UUID del recluso") @PathVariable UUID inmateId,
            @Parameter(description = "Nombre del oficial que realiza la asignación") @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String assignedBy) {
        return ResponseEntity.ok(cellService.assignInmate(cellId, inmateId, assignedBy));
    }

    @DeleteMapping("/{cellId}/position")
    @Operation(summary = "Eliminar posición de celda", description = "Elimina la posición de una celda en el mapa")
    public ResponseEntity<Void> removePosition(
            @Parameter(description = "UUID de la celda") @PathVariable UUID cellId) {
        cellPositionService.deleteByCellId(cellId);
        return ResponseEntity.noContent().build();
    }
}
