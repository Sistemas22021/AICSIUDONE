package com.nexocriminal.ubicacion.infrastructure.persistence;

import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Traduce entre el modelo de dominio (Ubicacion) y la entidad JPA existente
 * (com.nexocriminal.domain.ubicacion.Ubicacion). Se referencia la entidad JPA
 * por su nombre completo para no chocar con el modelo de dominio (mismo nombre simple).
 */
@Component
public class UbicacionMapper {

    /** Entidad JPA -> modelo de dominio. */
    public Ubicacion toDomain(com.nexocriminal.domain.ubicacion.Ubicacion entity) {
        if (entity == null) return null;
        return new Ubicacion(
                entity.getId(),
                entity.getDireccion(),
                entity.getLatitud() != null ? entity.getLatitud() : 0.0,
                entity.getLongitud() != null ? entity.getLongitud() : 0.0,
                entity.getTipo(),
                entity.getNodoSospechoso() != null && entity.getNodoSospechoso(),
                entity.getCreadoEn()
        );
    }

    /** Modelo de dominio -> entidad JPA. */
    public com.nexocriminal.domain.ubicacion.Ubicacion toEntity(Ubicacion domain) {
        if (domain == null) return null;
        return com.nexocriminal.domain.ubicacion.Ubicacion.builder()
                .id(domain.getId())
                .direccion(domain.getDireccion())
                .latitud(domain.getLatitud())
                .longitud(domain.getLongitud())
                .tipo(domain.getTipo())
                .nodoSospechoso(domain.isNodoSospechoso())
                .creadoEn(domain.getCreadoEn() != null ? domain.getCreadoEn() : LocalDateTime.now())
                .build();
    }
}