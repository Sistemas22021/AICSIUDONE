package com.nexocriminal.domain.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByDocumento(String documento);

    List<Persona> findByRol(RolPersona rol);

    /**
     * Encuentra intermediarios entre dos personas usando BFS en relaciones
     * (camino de 2 grados: victima -> intermediario -> sospechoso).
     */
    @Query(value = """
        WITH RECURSIVE amigos AS (
            SELECT persona_a_id AS origen, persona_b_id AS destino, 1 AS grado
            FROM relacion WHERE persona_a_id = :victimaId
            UNION
            SELECT a.origen, r.persona_b_id, a.grado + 1
            FROM amigos a JOIN relacion r ON a.destino = r.persona_a_id
            WHERE a.grado < 2
        )
        SELECT DISTINCT p.*
        FROM persona p
        WHERE p.id IN (
            SELECT a1.destino
            FROM amigos a1
            JOIN amigos a2 ON a1.destino = a2.origen
            WHERE a2.destino = :sospechosoId
        )
        """, nativeQuery = true)
    List<Persona> encontrarIntermediarios(@Param("victimaId") Long victimaId,
                                          @Param("sospechosoId") Long sospechosoId);
}
