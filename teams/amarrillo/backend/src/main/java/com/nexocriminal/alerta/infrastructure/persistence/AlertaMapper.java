package com.nexocriminal.alerta.infrastructure.persistence;

import com.nexocriminal.alerta.domain.model.Alerta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Traduce entre el modelo de dominio Alerta y la entidad JPA existente. */
@Component
public class AlertaMapper {

    public Alerta toDomain(com.nexocriminal.domain.alerta.Alerta entity) {
        if (entity == null) return null;
        return new Alerta(
                entity.getId(),
                entity.getTipo(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getNivelRiesgo(),
                entity.getVinculoId(),
                entity.getEstado(),
                entity.getCreadaEn()
        );
    }

    public com.nexocriminal.domain.alerta.Alerta toEntity(Alerta domain) {
        if (domain == null) return null;
        return com.nexocriminal.domain.alerta.Alerta.builder()
                .id(domain.getId())
                .tipo(domain.getTipo())
                .titulo(domain.getTitulo())
                .descripcion(domain.getDescripcion())
                .nivelRiesgo(domain.getNivelRiesgo())
                .vinculoId(domain.getVinculoId())
                .estado(domain.getEstado())
                .creadaEn(domain.getCreadaEn() != null ? domain.getCreadaEn() : LocalDateTime.now())
                .build();
    }
}