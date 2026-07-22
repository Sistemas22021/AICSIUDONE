package com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper;

import com.ccc.sistema_balistico.core.application.dto.ExpedientDTO;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.ExpedientEntity;

public class ExpedientMapper {

    public static ExpedientDTO toDTO(ExpedientEntity entity) {
        if (entity == null) {
            return null;
        }
        return ExpedientDTO.builder()
                .idExpedient(entity.getIdExpedient())
                .caseNumber(entity.getCaseNumber())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .isDelete(entity.getIsDelete())
                .build();
    }

    public static ExpedientEntity toEntity(ExpedientDTO dto) {
        if (dto == null) {
            return null;
        }
        return ExpedientEntity.builder()
                .idExpedient(dto.getIdExpedient())
                .caseNumber(dto.getCaseNumber())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .isDelete(dto.getIsDelete() != null ? dto.getIsDelete() : false)
                .build();
    }
}
