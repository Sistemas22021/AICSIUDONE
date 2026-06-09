package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransferRequestDto {
    private UUID id;
    private UUID inmateId;
    private String inmateName;
    private String inmateCedula;
    private UUID sourceCellId;
    private String sourceCellIdentifier;
    private UUID targetCellId;
    private String targetCellIdentifier;
    private String reason;
    private String status; // PENDIENTE, APROBADO, RECHAZADO
    private String requestedBy;
    private String resolvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String rejectionReason;
}
