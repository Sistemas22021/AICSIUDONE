package com.nexocriminal.desaparecida.infrastructure.persistence;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Traduce entre el modelo de dominio Desaparecida y la entidad JPA
 * PersonaDesaparecida existente. Las fotos NO se tocan aca (las maneja el
 * servicio de fotos); el mapper preserva la coleccion existente al actualizar.
 */
@Component
public class DesaparecidaMapper {

    public Desaparecida toDomain(com.nexocriminal.domain.desaparecida.PersonaDesaparecida e) {
        if (e == null) return null;
        return new Desaparecida(
                e.getId(),
                e.getDocumento(),
                e.getNombre(),
                e.getApellido(),
                e.getAlias(),
                e.getFechaNacimiento(),
                e.getGenero(),
                e.getEstaturaCm(),
                e.getPesoKg(),
                e.getContextura(),
                e.getColorCabello(),
                e.getColorOjos(),
                e.getSeñasParticulares(),
                e.getRopaAlDesaparecer(),
                e.getFotoUrl(),
                e.getFechaDesaparicion(),
                e.getUltimaUbicacion() != null ? e.getUltimaUbicacion().getId() : null,
                e.getCircunstancias(),
                e.getReportanteNombre(),
                e.getReportanteTelefono(),
                e.getReportanteRelacion(),
                e.getEstado(),
                e.getPrioridad(),
                e.getAnalisisIA(),
                e.getZonasBusquedaIA(),
                e.getFechaResolucion(),
                e.getCreadoEn(),
                e.getActualizadoEn()
        );
    }

    /**
     * Modelo de dominio -> entidad JPA. Si se pasa la entidad existente, se
     * preservan campos no manejados por el dominio (fotos, analisisIA, etc.).
     */
    public com.nexocriminal.domain.desaparecida.PersonaDesaparecida toEntity(
            Desaparecida d, com.nexocriminal.domain.desaparecida.PersonaDesaparecida existente) {
        if (d == null) return null;

        com.nexocriminal.domain.desaparecida.PersonaDesaparecida e =
                (existente != null) ? existente : new com.nexocriminal.domain.desaparecida.PersonaDesaparecida();

        e.setId(d.getId());
        e.setDocumento(d.getDocumento());
        e.setNombre(d.getNombre());
        e.setApellido(d.getApellido());
        e.setAlias(d.getAlias());
        e.setFechaNacimiento(d.getFechaNacimiento());
        e.setGenero(d.getGenero());
        e.setEstaturaCm(d.getEstaturaCm());
        e.setPesoKg(d.getPesoKg());
        e.setContextura(d.getContextura());
        e.setColorCabello(d.getColorCabello());
        e.setColorOjos(d.getColorOjos());
        e.setSeñasParticulares(d.getSenasParticulares());
        e.setRopaAlDesaparecer(d.getRopaAlDesaparecer());
        e.setFotoUrl(d.getFotoUrl());
        e.setFechaDesaparicion(d.getFechaDesaparicion());

        if (d.getUltimaUbicacionId() != null) {
            Ubicacion u = new Ubicacion();
            u.setId(d.getUltimaUbicacionId());
            e.setUltimaUbicacion(u);
        } else {
            e.setUltimaUbicacion(null);
        }

        e.setCircunstancias(d.getCircunstancias());
        e.setReportanteNombre(d.getReportanteNombre());
        e.setReportanteTelefono(d.getReportanteTelefono());
        e.setReportanteRelacion(d.getReportanteRelacion());
        e.setEstado(d.getEstado());
        e.setPrioridad(d.getPrioridad());
        e.setAnalisisIA(d.getAnalisisIA());
        e.setZonasBusquedaIA(d.getZonasBusquedaIA());
        e.setFechaResolucion(d.getFechaResolucion());
        e.setCreadoEn(d.getCreadoEn() != null ? d.getCreadoEn() : LocalDateTime.now());
        e.setActualizadoEn(d.getActualizadoEn() != null ? d.getActualizadoEn() : LocalDateTime.now());

        return e;
    }
}