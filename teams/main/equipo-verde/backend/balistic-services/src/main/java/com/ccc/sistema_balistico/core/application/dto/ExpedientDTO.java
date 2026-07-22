package com.ccc.sistema_balistico.core.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpedientDTO {
    private Long idExpedient;
    private String caseNumber;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private Boolean isDelete;
}
