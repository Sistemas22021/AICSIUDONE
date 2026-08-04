package com.guardia.core.repository;

import com.guardia.core.model.FirmaConductual;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FirmaConductualRepository extends JpaRepository<FirmaConductual, Long> {

    Optional<FirmaConductual> findByExpedienteIdAndVigenteTrue(Long expedienteId);

    List<FirmaConductual> findByExpedienteIdOrderByVersionDesc(Long expedienteId);

    /**
     * Búsqueda de texto plano sobre los 5 campos (HU: "la firma conductual es
     * indexada para búsqueda en el componente de detección de patrones").
     * Usa la columna generada busqueda_tsv + índice GIN (ver DDL). Nativa
     * porque tsvector/plainto_tsquery no tienen equivalente en JPQL.
     */
    @Query(value = """
            SELECT * FROM firmas_conductuales
            WHERE vigente = true
              AND busqueda_tsv @@ plainto_tsquery('spanish', :texto)
            ORDER BY fecha_registro DESC
            """, nativeQuery = true)
    List<FirmaConductual> buscarPorTexto(@Param("texto") String texto);

    /**
     * Búsqueda semántica por similitud vectorial (HU "Buscar patrones por MO
     * y firma conductual", CA2). Sólo considera la versión vigente de cada
     * expediente y sólo aquellas que ya tienen embedding calculado.
     */
    @Query("""
            SELECT f, cosine_distance(f.embedding, :embedding)
            FROM FirmaConductual f
            WHERE f.vigente = true AND f.embedding IS NOT NULL
            ORDER BY cosine_distance(f.embedding, :embedding) ASC
            """)
    List<Object[]> buscarPorEmbedding(@Param("embedding") float[] embedding, Pageable pageable);
}
