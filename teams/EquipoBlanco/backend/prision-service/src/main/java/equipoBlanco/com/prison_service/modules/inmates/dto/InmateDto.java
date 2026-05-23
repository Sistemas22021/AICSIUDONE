package equipoBlanco.com.prison_service.modules.inmates.dto;

import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InmateDto {
    private UUID id;
    private String cedula;
    private String firstName;
    private String secondName;
    private String firstLastname;
    private String secondLastname;
    private LocalDate birthDate;
    private String crime;
    private String caseNumber;
    private String court;
    private LocalDate admissionDate;
    private Integer sentenceYears;
    private Integer sentenceMonths;
    private LocalDate estimatedReleaseDate;
    private String eyeColor;
    private String hairColor;
    private String bodyBuild;
    private Integer heightCm;
    private BigDecimal weightKg;
    private String distinguishingMarks;
    private String photoUrl;
    private String fingerprintUrl;
    private InmateStatus status;
    private List<BelongingDto> belongings;
}
