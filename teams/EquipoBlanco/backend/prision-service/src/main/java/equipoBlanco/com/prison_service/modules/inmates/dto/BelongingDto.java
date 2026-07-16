package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BelongingDto {
    private UUID id;
    private String description;
    private Integer quantity;
    private String observations;
    private String status;
}
