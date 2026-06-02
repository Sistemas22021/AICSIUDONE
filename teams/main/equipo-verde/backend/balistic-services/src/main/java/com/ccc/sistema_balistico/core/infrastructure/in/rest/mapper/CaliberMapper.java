package com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper;

import com.ccc.sistema_balistico.core.application.dto.CaliberDTO;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;

public class CaliberMapper {

    public static CaliberDTO toDTO(CaliberEntity entity) {
        if (entity == null) {
            return null;
        }
        return CaliberDTO.builder()
                .idCaliber(entity.getIdCaliber())
                .name(entity.getName())
                .build();
    }

    public static CaliberEntity toEntity(CaliberDTO dto) {
        if (dto == null) {
            return null;
        }
        CaliberEntity entity = new CaliberEntity();
        entity.setIdCaliber(dto.getIdCaliber());
        entity.setName(dto.getName());
        return entity;
    }
}
