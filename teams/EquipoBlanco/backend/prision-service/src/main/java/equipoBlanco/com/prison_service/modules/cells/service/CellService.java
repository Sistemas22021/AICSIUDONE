package equipoBlanco.com.prison_service.modules.cells.service;

import equipoBlanco.com.prison_service.modules.cells.dto.CellDto;
import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import equipoBlanco.com.prison_service.modules.cells.model.CellAssignment;
import equipoBlanco.com.prison_service.modules.cells.repository.CellAssignmentRepository;
import equipoBlanco.com.prison_service.modules.cells.repository.CellRepository;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CellService {

    private final CellRepository cellRepository;
    private final InmateRepository inmateRepository;
    private final CellAssignmentRepository cellAssignmentRepository;

    public List<CellDto> getAllCells() {
        return cellRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public List<CellDto> getPlacedCells() {
        return cellRepository.findPlacedCells().stream()
            .map(this::toDto)
            .toList();
    }

    public CellDto createCell(CellDto dto) {
        if (cellRepository.existsByIdentifier(dto.getIdentifier())) {
            throw new RuntimeException("Ya existe una celda con el identificador: " + dto.getIdentifier());
        }
        Cell cell = Cell.builder()
            .identifier(dto.getIdentifier())
            .maxCapacity(Cell.MAX_CAPACITY)
            .conductLevel(dto.getConductLevel())
            .lengthMeters(dto.getLengthMeters())
            .widthMeters(dto.getWidthMeters())
            .build();
        return toDto(cellRepository.save(cell));
    }

    public CellDto updateCell(UUID id, CellDto dto) {
        Cell cell = cellRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Celda no encontrada"));
        cell.setIdentifier(dto.getIdentifier());
        cell.setConductLevel(dto.getConductLevel());
        cell.setLengthMeters(dto.getLengthMeters());
        cell.setWidthMeters(dto.getWidthMeters());
        return toDto(cellRepository.save(cell));
    }

    public void deleteCell(UUID id) {
        Cell cell = cellRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Celda no encontrada"));
        int occupancy = inmateRepository.countByCellId(id);
        if (occupancy > 0) {
            throw new RuntimeException("No se puede eliminar la celda porque tiene reclusos activos");
        }
        cellRepository.delete(cell);
    }

    public CellDto assignInmate(UUID cellId, UUID inmateId, String assignedBy) {
        Cell cell = cellRepository.findById(cellId)
            .orElseThrow(() -> new RuntimeException("Celda no encontrada"));

        if (cell.isBlockedForInvestigation()) {
            throw new RuntimeException("La celda está bloqueada por investigación");
        }

        int occupancy = inmateRepository.countByCellId(cellId);
        if (occupancy >= cell.getMaxCapacity()) {
            throw new RuntimeException("La celda está llena");
        }

        Inmate inmate = inmateRepository.findById(inmateId)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado"));

        if (inmate.getStatus() == Inmate.InmateStatus.ACTIVO_CON_CELDA) {
            throw new RuntimeException("El recluso ya tiene celda asignada");
        }

        inmate.setCell(cell);
        inmate.setStatus(Inmate.InmateStatus.ACTIVO_CON_CELDA);
        inmateRepository.save(inmate);

        CellAssignment assignment = CellAssignment.builder()
            .inmate(inmate)
            .cell(cell)
            .assignedBy(assignedBy)
            .assignedAt(LocalDateTime.now())
            .build();
        cellAssignmentRepository.save(assignment);

        return toDto(cell);
    }

    public CellDto unlockCell(UUID cellId) {
        Cell cell = cellRepository.findById(cellId)
            .orElseThrow(() -> new RuntimeException("Celda no encontrada"));

        if (!cell.isBlockedForInvestigation()) {
            throw new RuntimeException("La celda no se encuentra bloqueada");
        }

        cell.setBlockedForInvestigation(false);
        return toDto(cellRepository.save(cell));
    }

    private CellDto toDto(Cell cell) {
        int occupancy = inmateRepository.countByCellId(cell.getId());
        String status = cell.isBlockedForInvestigation() ? "BLOQUEADA"
            : occupancy >= cell.getMaxCapacity() ? "LLENO"
            : occupancy >= cell.getMaxCapacity() * 0.8 ? "LIMITE"
            : "DISPONIBLE";

        return CellDto.builder()
            .id(cell.getId())
            .identifier(cell.getIdentifier())
            .maxCapacity(cell.getMaxCapacity())
            .conductLevel(cell.getConductLevel())
            .lengthMeters(cell.getLengthMeters())
            .widthMeters(cell.getWidthMeters())
            .currentOccupancy(occupancy)
            .occupancyStatus(status)
            .blockedForInvestigation(cell.isBlockedForInvestigation())
            .build();
    }
}
