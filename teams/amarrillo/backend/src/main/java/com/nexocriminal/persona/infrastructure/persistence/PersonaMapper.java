package com.nexocriminal.persona.infrastructure.persistence;

import com.nexocriminal.persona.domain.model.Persona;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Traduce entre el modelo de dominio Persona y la entidad JPA existente
 * (com.nexocriminal.domain.persona.Persona). Se usa el nombre completo de la
 * entidad JPA para no chocar con el modelo de dominio (mismo nombre simple).
 */
@Component
public class PersonaMapper {

    /** Entidad JPA -> modelo de dominio. */
    public Persona toDomain(com.nexocriminal.domain.persona.Persona entity) {
        if (entity == null) return null;
        return new Persona(
                entity.getId(),
                entity.getDocumento(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getAlias(),
                entity.getFechaNacimiento(),
                entity.getRol(),
                entity.getTelefono(),
                entity.getEstado(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    /** Modelo de dominio -> entidad JPA. */
    public com.nexocriminal.domain.persona.Persona toEntity(Persona domain) {
        if (domain == null) return null;
        return com.nexocriminal.domain.persona.Persona.builder()
                .id(domain.getId())
                .documento(domain.getDocumento())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .alias(domain.getAlias())
                .fechaNacimiento(domain.getFechaNacimiento())
                .rol(domain.getRol())
                .telefono(domain.getTelefono())
                .estado(domain.getEstado() != null ? domain.getEstado() : "ACTIVO")
                .creadoEn(domain.getCreadoEn() != null ? domain.getCreadoEn() : LocalDateTime.now())
                .actualizadoEn(domain.getActualizadoEn() != null ? domain.getActualizadoEn() : LocalDateTime.now())
                .build();
    }
}