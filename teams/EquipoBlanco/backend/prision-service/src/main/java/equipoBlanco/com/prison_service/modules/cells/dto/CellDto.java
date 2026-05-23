package equipoBlanco.com.prison_service.modules.cells.dto;

import equipoBlanco.com.prison_service.modules.cells.model.Cell.ConductLevel;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CellDto {
    private UUID id;

    @NotBlank(message = "El identificador es obligatorio")
    private String identifier;

    @NotNull @Min(1)
    private Integer maxCapacity;

    @NotNull
    private ConductLevel conductLevel;

    private BigDecimal lengthMeters;
    private BigDecimal widthMeters;

    private Integer currentOccupancy;
    private String occupancyStatus;
}
