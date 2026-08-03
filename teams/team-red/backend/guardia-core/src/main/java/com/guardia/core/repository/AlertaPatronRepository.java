package com.guardia.core.repository;

import com.guardia.core.model.AlertaPatron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para alertas internas de patrón de MO
 */
@Repository
public interface AlertaPatronRepository extends JpaRepository<AlertaPatron, Long> {

    /** Todas las alertas, más reciente primero — base del panel del Guardia  */
    List<AlertaPatron> findAllByOrderByFechaGeneracionDesc();

    /** Bandeja personal: alertas donde el usuario aparece como notificado */
    @Query("""
            SELECT a FROM AlertaPatron a
            WHERE :investigadorId MEMBER OF a.investigadoresNotificados
            ORDER BY a.fechaGeneracion DESC
            """)
    List<AlertaPatron> findByInvestigadorNotificado(@Param("investigadorId") UUID investigadorId);

    /** Soporte de deduplicación: ¿ya existe una alerta para este mismo conjunto de expedientes dentro de la ventana? */
    boolean existsByClaveDeduplicacionAndFechaGeneracionAfter(String claveDeduplicacion, LocalDateTime desde);
}