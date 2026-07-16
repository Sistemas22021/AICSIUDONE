package equipoBlanco.com.prison_service.modules.cells.service;

import equipoBlanco.com.prison_service.modules.cells.dto.PrisonMapDto;
import equipoBlanco.com.prison_service.modules.cells.model.PrisonMap;
import equipoBlanco.com.prison_service.modules.cells.repository.PrisonMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrisonMapService {

    private final PrisonMapRepository repository;

    public PrisonMapDto getMap(Integer floorNumber) {
        return repository.findByFloorNumber(floorNumber)
            .map(this::toDto)
            .orElse(null);
    }

    @Transactional
    public PrisonMapDto saveMap(Integer floorNumber, String backgroundImage) {
        PrisonMap map = repository.findByFloorNumber(floorNumber)
            .orElse(PrisonMap.ofFloor(floorNumber));

        map.setBackgroundImage(backgroundImage);
        return toDto(repository.save(map));
    }

    @Transactional
    public void deleteMap(Integer floorNumber) {
        repository.findByFloorNumber(floorNumber)
            .ifPresent(repository::delete);
    }

    private PrisonMapDto toDto(PrisonMap map) {
        return PrisonMapDto.builder()
            .floorNumber(map.getFloorNumber())
            .backgroundImage(map.getBackgroundImage())
            .build();
    }
}
