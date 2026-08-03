package com.guardia.core.repository;

import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repositorio JPA para propuestas de Modus Operandi (HU2/HU3).
 */
public interface PropuestaModusOperandiRepository extends JpaRepository<PropuestaModusOperandi, Long> {

    Optional<PropuestaModusOperandi> findByExpedienteIdAndVigenteTrue(Long expedienteId);

    /** Historial completo (vigente + versiones anteriores), más reciente primero. HU3 CA5. */
    List<PropuestaModusOperandi> findByExpedienteIdOrderByVersionDesc(Long expedienteId);

    @Query("""
            SELECT p, cosine_distance(p.embedding, :embedding)
            FROM PropuestaModusOperandi p
            WHERE p.vigente = true
              AND p.embedding IS NOT NULL
              AND p.estado IN :estadosValidados
            ORDER BY cosine_distance(p.embedding, :embedding) ASC
            """)
    List<Object[]> buscarPorEmbeddingMOValidado(@Param("embedding") float[] embedding,
                                                @Param("estadosValidados") List<EstadoPropuestaMO> estadosValidados,
                                                Pageable pageable);
}