package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExpedient;
    
    private String caseNumber;
    private String description;
    private String status;
    
    private LocalDateTime createdAt;
    
    @Builder.Default
    private Boolean isDelete = false;
}
