package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpedientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCaliber;
    private LocalDateTime createdAt;
    private Boolean isDelete;
}
