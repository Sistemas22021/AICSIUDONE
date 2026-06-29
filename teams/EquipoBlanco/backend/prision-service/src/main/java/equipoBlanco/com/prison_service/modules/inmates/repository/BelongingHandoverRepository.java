package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.BelongingHandover;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BelongingHandoverRepository extends JpaRepository<BelongingHandover, UUID> {
    List<BelongingHandover> findByInmateId(UUID inmateId);
}
