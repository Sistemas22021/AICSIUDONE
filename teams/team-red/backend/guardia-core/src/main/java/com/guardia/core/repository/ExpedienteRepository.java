package com.guardia.core.repository;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.enums.EstadoExpediente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repositorio JPA para expedientes.
 * Incluye búsquedas por folio, estado y creador.
 */
public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
    Optional<Expediente> findByFolio(String folio);
    List<Expediente> findByEstadoExpediente(EstadoExpediente estado);
    List<Expediente> findByCreadoPorId(Long creadoPorId);

    @Query("""
            SELECT e, cosine_distance(e.embedding, :embedding)
            FROM Expediente e
            WHERE e.id <> :expedienteId AND e.embedding IS NOT NULL
            ORDER BY cosine_distance(e.embedding, :embedding) ASC
            """)
    List<Object[]> buscarSimilaresPorEmbedding(@Param("expedienteId") Long expedienteId,
                                               @Param("embedding") float[] embedding,
                                               Pageable pageable);

    @Query("""
            SELECT cosine_distance(a.embedding, b.embedding)
            FROM Expediente a, Expediente b
            WHERE a.id = :idA AND b.id = :idB
            """)
    Double calcularDistanciaCoseno(@Param("idA") Long idA, @Param("idB") Long idB);
}