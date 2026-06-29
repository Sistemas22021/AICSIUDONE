package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IncidentParticipantDto {
    private UUID id;
    private UUID inmateId;
    private String inmateName;
    private String inmateCedula;
    private String role; // FALLECIDO, COHABITANTE
    private String initialStatus; // FALLECIDO, ILESO, LESIONADO, TRASLADADO_ENFERMERIA
    private String comments;
}
