package com.nexocriminal.suceso.infrastructure.persistence;

import com.nexocriminal.domain.persona.Persona;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.vehiculo.Vehiculo;
import com.nexocriminal.suceso.domain.model.Suceso;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Traduce entre el modelo de dominio Suceso y la entidad JPA existente.
 * Para las relaciones, crea proxies (solo id) que JPA usa para setear las FK.
 */
@Component
public class SucesoMapper {

    public Suceso toDomain(com.nexocriminal.domain.suceso.Suceso entity) {
        if (entity == null) return null;
        return new Suceso(
                entity.getId(),
                entity.getTipo(),
                entity.getFechaHora(),
                entity.getModusOperandi(),
                entity.getDescripcion(),
                entity.getVehiculo() != null ? entity.getVehiculo().getId() : null,
                entity.getVictima() != null ? entity.getVictima().getId() : null,
                entity.getUbicacion() != null ? entity.getUbicacion().getId() : null,
                entity.getUbicacionUltima() != null ? entity.getUbicacionUltima().getId() : null,
                entity.getCreadoEn()
        );
    }

    public com.nexocriminal.domain.suceso.Suceso toEntity(Suceso domain) {
        if (domain == null) return null;

        Vehiculo vehiculo = null;
        if (domain.getVehiculoId() != null) {
            vehiculo = new Vehiculo();
            vehiculo.setId(domain.getVehiculoId());
        }

        Persona victima = null;
        if (domain.getVictimaId() != null) {
            victima = new Persona();
            victima.setId(domain.getVictimaId());
        }

        Ubicacion ubicacion = null;
        if (domain.getUbicacionId() != null) {
            ubicacion = new Ubicacion();
            ubicacion.setId(domain.getUbicacionId());
        }

        Ubicacion ubicacionUltima = null;
        if (domain.getUbicacionUltimaId() != null) {
            ubicacionUltima = new Ubicacion();
            ubicacionUltima.setId(domain.getUbicacionUltimaId());
        }

        return com.nexocriminal.domain.suceso.Suceso.builder()
                .id(domain.getId())
                .tipo(domain.getTipo())
                .fechaHora(domain.getFechaHora())
                .modusOperandi(domain.getModusOperandi())
                .descripcion(domain.getDescripcion())
                .vehiculo(vehiculo)
                .victima(victima)
                .ubicacion(ubicacion)
                .ubicacionUltima(ubicacionUltima)
                .creadoEn(domain.getCreadoEn() != null ? domain.getCreadoEn() : LocalDateTime.now())
                .build();
    }
}