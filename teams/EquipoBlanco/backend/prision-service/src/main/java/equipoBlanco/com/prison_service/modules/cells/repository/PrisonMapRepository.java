package equipoBlanco.com.prison_service.modules.cells.repository;

import equipoBlanco.com.prison_service.modules.cells.model.PrisonMap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PrisonMapRepository extends JpaRepository<PrisonMap, UUID> {
    Optional<PrisonMap> findByFloorNumber(Integer floorNumber);
    boolean existsByFloorNumber(Integer floorNumber);
}
