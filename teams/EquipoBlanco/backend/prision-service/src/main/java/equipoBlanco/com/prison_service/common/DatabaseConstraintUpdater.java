package equipoBlanco.com.prison_service.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Updates database CHECK constraints after Hibernate has finished DDL updates.
 * This is needed because Hibernate ddl-auto=update does not modify existing constraints.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseConstraintUpdater {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void updateConstraints() {
        try {
            // Drop the old constraint that doesn't include ACTIVO_SALIDA_TEMPORAL
            jdbcTemplate.execute(
                "ALTER TABLE inmates DROP CONSTRAINT IF EXISTS inmates_status_check"
            );

            // Recreate with all valid status values
            jdbcTemplate.execute(
                "ALTER TABLE inmates ADD CONSTRAINT inmates_status_check " +
                "CHECK (status IN ('ACTIVO_SIN_CELDA', 'ACTIVO_CON_CELDA', 'ACTIVO_SALIDA_TEMPORAL', 'EGRESADO'))"
            );

            log.info("✅ Constraint inmates_status_check actualizado exitosamente con ACTIVO_SALIDA_TEMPORAL.");
        } catch (Exception e) {
            log.warn("⚠️ No se pudo actualizar el constraint inmates_status_check: {}", e.getMessage());
        }
    }
}
