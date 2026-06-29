package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.DeathReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DeathReportRepository extends JpaRepository<DeathReport, UUID> {
    Optional<DeathReport> findByInmateId(UUID inmateId);
}
