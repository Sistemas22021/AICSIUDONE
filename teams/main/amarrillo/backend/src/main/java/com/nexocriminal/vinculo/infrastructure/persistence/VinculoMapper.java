package com.nexocriminal.vinculo.infrastructure.persistence;

import com.nexocriminal.vinculo.domain.model.Vinculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Traduce entre el modelo de dominio Vinculo y la entidad JPA existente. */
@Component
public class VinculoMapper {

    public Vinculo toDomain(com.nexocriminal.domain.vinculo.Vinculo entity) {
        if (entity == null) return null;
        return new Vinculo(
                entity.getId(),
                entity.getTipoOrigen(),
                entity.getIdOrigen(),
                entity.getTipoDestino(),
                entity.getIdDestino(),
                entity.getReglaDetectada(),
                entity.getScore() != null ? entity.getScore() : BigDecimal.valueOf(1.0),
                entity.getActivo() != null ? entity.getActivo() : Boolean.TRUE,
                entity.getDetectadoEn() != null ? entity.getDetectadoEn() : LocalDateTime.now()
        );
    }
}