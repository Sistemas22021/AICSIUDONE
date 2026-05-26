package equipoBlanco.com.prison_service.modules.cells.repository;

import equipoBlanco.com.prison_service.modules.cells.model.CellAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CellAssignmentRepository extends JpaRepository<CellAssignment, UUID> {
}
