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
            // Drop the old constraint
            jdbcTemplate.execute(
                "ALTER TABLE inmates DROP CONSTRAINT IF EXISTS inmates_status_check"
            );

            // Recreate with all valid status values
            jdbcTemplate.execute(
                "ALTER TABLE inmates ADD CONSTRAINT inmates_status_check " +
                "CHECK (status IN ('ACTIVO_SIN_CELDA', 'ACTIVO_CON_CELDA', 'ACTIVO_SALIDA_TEMPORAL', 'EGRESADO', 'PENDIENTE_REUBICACION_EMERGENCIA'))"
            );

            // Ensure cell columns exist
            try {
                jdbcTemplate.execute("ALTER TABLE cells ADD COLUMN IF NOT EXISTS blocked_for_investigation BOOLEAN DEFAULT FALSE");
            } catch (Exception e) {
                log.warn("⚠️ Column blocked_for_investigation already exists or could not be created: {}", e.getMessage());
            }

            // Ensure belonging columns exist
            try {
                jdbcTemplate.execute("ALTER TABLE belongings ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ALMACENADO'");
                jdbcTemplate.execute("ALTER TABLE belongings ADD COLUMN IF NOT EXISTS handover_id UUID");
            } catch (Exception e) {
                log.warn("⚠️ Columns for belongings could not be verified: {}", e.getMessage());
            }

            log.info("✅ Base de datos verificada y constraints actualizados exitosamente.");
        } catch (Exception e) {
            log.warn("⚠️ No se pudo actualizar el constraint inmates_status_check: {}", e.getMessage());
        }
    }
}
