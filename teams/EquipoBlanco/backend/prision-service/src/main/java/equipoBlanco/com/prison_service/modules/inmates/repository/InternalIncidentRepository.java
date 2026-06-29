package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.InternalIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InternalIncidentRepository extends JpaRepository<InternalIncident, UUID> {
    Optional<InternalIncident> findByCode(String code);
}
