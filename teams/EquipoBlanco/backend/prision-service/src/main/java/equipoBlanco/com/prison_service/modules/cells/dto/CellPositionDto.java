package equipoBlanco.com.prison_service.modules.cells.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CellPositionDto {
    private String cellId;
    private Integer floorNumber;
    private Double posX;
    private Double posY;
    private Integer radius;
}
