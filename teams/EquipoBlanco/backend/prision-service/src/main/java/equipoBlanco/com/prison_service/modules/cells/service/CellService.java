package equipoBlanco.com.prison_service.modules.cells.service;

import equipoBlanco.com.prison_service.modules.cells.dto.CellDto;
import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import equipoBlanco.com.prison_service.modules.cells.repository.CellRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CellService {

    private final CellRepository cellRepository;
    private final InmateRepository inmateRepository;

    public List<CellDto> getAllCells() {
        return cellRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public CellDto createCell(CellDto dto) {
        if (cellRepository.existsByIdentifier(dto.getIdentifier())) {
            throw new RuntimeException("Ya existe una celda con el identificador: " + dto.getIdentifier());
        }
        Cell cell = Cell.builder()
            .identifier(dto.getIdentifier())
            .maxCapacity(dto.getMaxCapacity())
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
        cell.setMaxCapacity(dto.getMaxCapacity());
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

    private CellDto toDto(Cell cell) {
        int occupancy = inmateRepository.countByCellId(cell.getId());
        String status = occupancy >= cell.getMaxCapacity() ? "LLENO"
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
            .build();
    }
}
