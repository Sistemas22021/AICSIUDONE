package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InternalIncidentDto {
    private UUID id;
    private String code;
    private String cellIdentifier;
    private UUID cellId;
    private String description;
    private LocalDateTime incidentDate;
    private String reporter;
    private String status;
    private List<IncidentParticipantDto> participants;
}
