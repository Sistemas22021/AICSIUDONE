package com.nexocriminal.vehiculo.infrastructure.persistence;

import com.nexocriminal.domain.persona.Persona;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Traduce entre el modelo de dominio Vehiculo y la entidad JPA existente.
 * Para el propietario, el dominio solo maneja el id; al mapear a entidad se
 * crea una Persona "proxy" con solo el id (suficiente para que JPA setee la FK).
 */
@Component
public class VehiculoMapper {

    /** Entidad JPA -> modelo de dominio. */
    public Vehiculo toDomain(com.nexocriminal.domain.vehiculo.Vehiculo entity) {
        if (entity == null) return null;
        Long propId = (entity.getPropietario() != null) ? entity.getPropietario().getId() : null;
        return new Vehiculo(
                entity.getId(),
                entity.getPlaca(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAnio(),
                entity.getColor(),
                entity.getEstado(),
                propId,
                entity.getCreadoEn()
        );
    }

    /** Modelo de dominio -> entidad JPA. */
    public com.nexocriminal.domain.vehiculo.Vehiculo toEntity(Vehiculo domain) {
        if (domain == null) return null;

        Persona propietario = null;
        if (domain.getPropietarioId() != null) {
            // Proxy: solo el id, suficiente para que JPA guarde la relacion (FK).
            propietario = new Persona();
            propietario.setId(domain.getPropietarioId());
        }

        return com.nexocriminal.domain.vehiculo.Vehiculo.builder()
                .id(domain.getId())
                .placa(domain.getPlaca())
                .marca(domain.getMarca())
                .modelo(domain.getModelo())
                .anio(domain.getAnio())
                .color(domain.getColor())
                .estado(domain.getEstado())
                .propietario(propietario)
                .creadoEn(domain.getCreadoEn() != null ? domain.getCreadoEn() : LocalDateTime.now())
                .build();
    }
}