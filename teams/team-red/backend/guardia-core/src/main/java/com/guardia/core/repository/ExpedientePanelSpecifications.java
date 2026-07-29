package com.guardia.core.repository.specification;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.enums.EstadoExpediente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Filtros para el listado paginado del panel de expedientes. Distinta de
 * ExpedienteSpecifications (búsqueda geográfica por radio/categoría), que ya
 * existe en este mismo paquete — nombre distinto para evitar choque de import.
 */
public final class ExpedientePanelSpecifications {

    private static final Set<EstadoExpediente> ESTADOS_INACTIVOS = Set.of(
            EstadoExpediente.BORRADOR,
            EstadoExpediente.CERRADO,
            EstadoExpediente.SOLICITUD_DE_REAPERTURA,
            EstadoExpediente.ARCHIVADO
    );

    private ExpedientePanelSpecifications() {
    }

    public static Specification<Expediente> conEstatus(String estatus) {
        if (estatus == null || estatus.isBlank()) return null;

        if ("ACTIVO".equalsIgnoreCase(estatus)) {
            return (root, query, cb) -> cb.not(root.get("estadoExpediente").in(ESTADOS_INACTIVOS));
        }
        EstadoExpediente estado = EstadoExpediente.valueOf(estatus.toUpperCase());
        return (root, query, cb) -> cb.equal(root.get("estadoExpediente"), estado);
    }

    public static Specification<Expediente> conBusqueda(String texto) {
        if (texto == null || texto.isBlank()) return null;
        String like = "%" + texto.toLowerCase().trim() + "%";

        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.like(cb.lower(root.get("folio")), like));
            predicados.add(cb.like(cb.lower(root.get("municipio")), like));
            predicados.add(cb.like(cb.lower(root.get("sector")), like));
            predicados.add(cb.like(cb.lower(root.join("tipoDelito", jakarta.persistence.criteria.JoinType.LEFT).get("nombre")), like));
            predicados.add(cb.like(cb.lower(root.join("subtipoDelito", jakarta.persistence.criteria.JoinType.LEFT).get("nombre")), like));
            return cb.or(predicados.toArray(new Predicate[0]));
        };
    }
}