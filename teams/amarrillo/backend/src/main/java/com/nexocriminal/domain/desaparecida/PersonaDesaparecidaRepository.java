package com.nexocriminal.domain.desaparecida;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonaDesaparecidaRepository extends JpaRepository<PersonaDesaparecida, Long> {

    List<PersonaDesaparecida> findByEstado(EstadoDesaparicion estado);

    List<PersonaDesaparecida> findByPrioridad(PrioridadDesaparicion prioridad);

    List<PersonaDesaparecida> findByEstadoAndPrioridad(EstadoDesaparicion estado, PrioridadDesaparicion prioridad);

    List<PersonaDesaparecida> findByFechaDesaparicionAfter(LocalDateTime fecha);

    /**
     * Encuentra desapariciones cuya última ubicación está dentro de un radio.
     * Usa la fórmula Haversine simplificada.
     */
    @Query(value = """
        SELECT pd.* FROM persona_desaparecida pd
        JOIN ubicacion u ON pd.ultima_ubicacion_id = u.id
        WHERE pd.estado = 'BUSCADA'
        AND (
            6371000 * acos(
                LEAST(1, GREATEST(-1,
                    cos(radians(:lat)) * cos(radians(u.latitud)) *
                    cos(radians(u.longitud) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(u.latitud))
                ))
            )
        ) <= :radioMetros
        """, nativeQuery = true)
    List<PersonaDesaparecida> findEnRadio(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radioMetros") int radioMetros
    );

    /**
     * Cuenta desapariciones por estado (para dashboard).
     */
    @Query("SELECT pd.estado, COUNT(pd) FROM PersonaDesaparecida pd GROUP BY pd.estado")
    List<Object[]> contarPorEstado();
}