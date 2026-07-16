package equipoBlanco.com.prison_service.modules.cells.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MapWithPositionsDto {
    private PrisonMapDto map;
    private List<CellPositionDto> positions;
}
