package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeathReportDto {
    private UUID id;
    private UUID inmateId;
    private String deceaseType; // NATURAL, NO_NATURAL
    private LocalDateTime dateTimeFound;
    private String description;
}
