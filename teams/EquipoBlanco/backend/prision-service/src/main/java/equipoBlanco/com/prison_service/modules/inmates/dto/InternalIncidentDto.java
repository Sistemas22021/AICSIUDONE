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

    // Campos de conclusión
    private String conclusionType;
    private String causaMedicaDefinitiva;
    private String autopsiaProtocolo;
    private String fiscaliaExpediente;
    private UUID responsableInmateId;
    private String responsablePersonal;
    private Boolean responsableNoAplica;
    private LocalDateTime concludedAt;
    private String concludedBy;

    // Condena adicional imputada al responsable
    private Integer additionalSentenceYears;
    private Integer additionalSentenceMonths;
    /** Nombre del recluso al que se imputó la condena adicional */
    private String responsableInmateName;
}


