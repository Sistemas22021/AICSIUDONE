package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BelongingHandoverDto {
    private UUID id;
    private UUID inmateId;
    private LocalDateTime handoverDate;
    private String recipientName;
    private String recipientCedula;
    private String authorizedBy;
    private List<UUID> belongingIds; // list of belongings to deliver
}
