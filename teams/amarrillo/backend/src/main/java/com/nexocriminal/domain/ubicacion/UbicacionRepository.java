package com.nexocriminal.domain.ubicacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    List<Ubicacion> findByTipoIn(List<TipoUbicacion> tipos);

    /**
     * Distancia aproximada en metros entre dos puntos (formula de Haversine simplificada).
     * Para uso en consultas sin PostGIS. Si hay PostGIS, usar ST_DWithin es mas eficiente.
     */
    @Query(value = """
        SELECT u.* FROM ubicacion u
        WHERE (
            6371000 * acos(
                cos(radians(:lat)) * cos(radians(u.latitud))
                * cos(radians(u.longitud) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(u.latitud))
            )
        ) <= :radioMetros
        """, nativeQuery = true)
    List<Ubicacion> buscarEnRadio(@Param("lat") Double lat,
                                  @Param("lng") Double lng,
                                  @Param("radioMetros") Integer radioMetros);
}
