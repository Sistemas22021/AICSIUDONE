package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.IncidentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface IncidentParticipantRepository extends JpaRepository<IncidentParticipant, UUID> {
    List<IncidentParticipant> findByIncidentId(UUID incidentId);
    List<IncidentParticipant> findByInmateId(UUID inmateId);
}
