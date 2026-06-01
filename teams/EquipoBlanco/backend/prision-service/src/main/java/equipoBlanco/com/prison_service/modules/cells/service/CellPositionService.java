package equipoBlanco.com.prison_service.modules.cells.service;

import equipoBlanco.com.prison_service.modules.cells.dto.CellPositionDto;
import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import equipoBlanco.com.prison_service.modules.cells.model.CellPosition;
import equipoBlanco.com.prison_service.modules.cells.model.PrisonMap;
import equipoBlanco.com.prison_service.modules.cells.repository.CellPositionRepository;
import equipoBlanco.com.prison_service.modules.cells.repository.CellRepository;
import equipoBlanco.com.prison_service.modules.cells.repository.PrisonMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CellPositionService {

    private final CellPositionRepository repository;
    private final CellRepository cellRepository;
    private final PrisonMapRepository prisonMapRepository;

    public List<CellPositionDto> getPositionsByFloor(Integer floorNumber) {
        return repository.findByPrisonMapFloorNumber(floorNumber).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional
    public List<CellPositionDto> savePositions(Integer floorNumber, List<CellPositionDto> dtos) {
        PrisonMap prisonMap = prisonMapRepository.findByFloorNumber(floorNumber)
            .orElseGet(() -> prisonMapRepository.save(
                PrisonMap.ofFloor(floorNumber)
            ));

        List<CellPosition> saved = dtos.stream()
            .map(dto -> {
                Cell cell = cellRepository.findById(UUID.fromString(dto.getCellId()))
                    .orElseThrow(() -> new RuntimeException("Celda no encontrada: " + dto.getCellId()));
                CellPosition pos = repository.findByCellId(cell.getId())
                    .orElse(CellPosition.builder().cell(cell).build());
                pos.setCell(cell);
                pos.setPrisonMap(prisonMap);
                pos.setPosX(dto.getPosX());
                pos.setPosY(dto.getPosY());
                pos.setRadius(dto.getRadius() != null ? dto.getRadius() : 18);
                return repository.save(pos);
            })
            .toList();

        return saved.stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteByCellId(UUID cellId) {
        repository.deleteByCellId(cellId);
    }

    private CellPositionDto toDto(CellPosition pos) {
        return CellPositionDto.builder()
            .cellId(pos.getCell().getId().toString())
            .floorNumber(pos.getPrisonMap().getFloorNumber())
            .posX(pos.getPosX())
            .posY(pos.getPosY())
            .radius(pos.getRadius())
            .build();
    }
}
