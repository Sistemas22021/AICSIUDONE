package com.guardia.core.repository.specification;

import com.guardia.core.dto.request.ExpedienteFiltroRequest;
import com.guardia.core.model.DelitoEnExpediente;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Localizacion;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ExpedienteSpecifications {

    private ExpedienteSpecifications() {
    }

    /** Combina, con AND, todos los criterios presentes en el filtro recibido. */
    public static Specification<Expediente> combinar(ExpedienteFiltroRequest filtro) {
        return Specification
                .where(distinctPorElJoinDeDelitos())   // ← nuevo, primero en la cadena
                .and(porTiposDelito(filtro.tiposDelito()))
                .and(porMunicipio(filtro.municipio()))
                .and(porColonia(filtro.colonia()))
                .and(dentroDeRadio(filtro.latitud(), filtro.longitud(), filtro.radioKm()))
                .and(porRangoDeFechas(filtro.fechaDesde(), filtro.fechaHasta()));
    }

    /** Expedientes cuyo tipo de delito coincide con alguno de los indicados (uno o varios). */
    public static Specification<Expediente> porTiposDelito(List<String> tiposDelito) {
        if (tiposDelito == null || tiposDelito.isEmpty()) return neutra();

        List<String> normalizados = tiposDelito.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(t -> t.trim().toUpperCase())
                .toList();

        if (normalizados.isEmpty()) return neutra();

        return (root, query, cb) -> cb.upper(root.get("tipoDelito").get("nombre")).in(normalizados);
    }

    /** Expedientes cuyo municipio contiene el texto indicado (coincide en Localizacion o en el propio expediente). */
    public static Specification<Expediente> porMunicipio(String municipio) {
        if (municipio == null || municipio.isBlank()) return neutra();
        String patron = "%" + municipio.trim().toLowerCase() + "%";

        return (root, query, cb) -> {
            Join<Expediente, Localizacion> localizacion = joinLocalizacion(root);
            return cb.or(
                    cb.like(cb.lower(localizacion.get("municipio")), patron),
                    cb.like(cb.lower(root.get("municipio")), patron)
            );
        };
    }

    /** Expedientes cuya colonia/sector contiene el texto indicado. */
    public static Specification<Expediente> porColonia(String colonia) {
        if (colonia == null || colonia.isBlank()) return neutra();
        String patron = "%" + colonia.trim().toLowerCase() + "%";

        return (root, query, cb) -> {
            Join<Expediente, Localizacion> localizacion = joinLocalizacion(root);
            return cb.or(
                    cb.like(cb.lower(localizacion.get("sector")), patron),
                    cb.like(cb.lower(root.get("sector")), patron)
            );
        };
    }

    /**
     * Expedientes cuya localización cae dentro de un radio aproximado (en km) alrededor
     * de un punto, calculado en la base de datos con la fórmula del haversine.
     * Requiere los tres parámetros; si falta alguno, el criterio no se aplica.
     */
    public static Specification<Expediente> dentroDeRadio(Double latitud, Double longitud, Double radioKm) {
        if (latitud == null || longitud == null || radioKm == null) return neutra();

        return (root, query, cb) -> {
            Join<Expediente, Localizacion> localizacion = joinLocalizacion(root);
            Expression<Double> lat = localizacion.get("latitud");
            Expression<Double> lon = localizacion.get("longitud");

            Expression<Double> distanciaKm = cb.prod(6371.0, cb.function("acos", Double.class,
                    cb.sum(
                            cb.prod(
                                    cb.function("cos", Double.class, radianes(cb, cb.literal(latitud))),
                                    cb.prod(
                                            cb.function("cos", Double.class, radianes(cb, lat)),
                                            cb.function("cos", Double.class,
                                                    cb.diff(radianes(cb, lon), radianes(cb, cb.literal(longitud))))
                                    )
                            ),
                            cb.prod(
                                    cb.function("sin", Double.class, radianes(cb, cb.literal(latitud))),
                                    cb.function("sin", Double.class, radianes(cb, lat))
                            )
                    )
            ));

            return cb.and(
                    cb.isNotNull(lat),
                    cb.isNotNull(lon),
                    cb.le(distanciaKm, radioKm)
            );
        };
    }

    /** Expedientes cuya fecha del hecho cae dentro del rango [desde, hasta] (ambos límites opcionales). */
    public static Specification<Expediente> porRangoDeFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) return neutra();

        return (root, query, cb) -> {
            Predicate coincideEnRaiz = enRango(cb, root.get("fechaHecho"), desde, hasta);

            Join<Expediente, DelitoEnExpediente> delitosJoin = root.join("delitos", JoinType.LEFT);
            Predicate coincideEnDelito = enRango(cb, delitosJoin.get("fechaHoraHecho"), desde, hasta);

            return cb.or(coincideEnRaiz, coincideEnDelito);
        };
    }

    private static Predicate enRango(CriteriaBuilder cb, Expression<LocalDateTime> campo,
                                     LocalDate desde, LocalDate hasta) {
        List<Predicate> predicados = new ArrayList<>();
        predicados.add(cb.isNotNull(campo));
        if (desde != null) {
            predicados.add(cb.greaterThanOrEqualTo(campo, desde.atStartOfDay()));
        }
        if (hasta != null) {
            predicados.add(cb.lessThan(campo, hasta.plusDays(1).atStartOfDay()));
        }
        return cb.and(predicados.toArray(new Predicate[0]));
    }


    // ─── Helpers privados ───────────────────────────────────────────────────

    /** Specification "neutra": no añade ningún predicado (criterio ausente/vacío). */
    private static Specification<Expediente> neutra() {
        return (root, query, cb) -> null;
    }

    private static Expression<Double> radianes(CriteriaBuilder cb, Expression<Double> grados) {
        return cb.function("radians", Double.class, grados);
    }

    /** Reutiliza el join a Localizacion si ya fue añadido por otro criterio dentro de la misma consulta. */
    @SuppressWarnings("unchecked")
    private static Join<Expediente, Localizacion> joinLocalizacion(Root<Expediente> root) {
        return root.getJoins().stream()
                .filter(j -> "localizacion".equals(j.getAttribute().getName()))
                .map(j -> (Join<Expediente, Localizacion>) j)
                .findFirst()
                .orElseGet(() -> root.join("localizacion", JoinType.LEFT));
    }

    private static Specification<Expediente> distinctPorElJoinDeDelitos() {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            return null;
        };
    }

}