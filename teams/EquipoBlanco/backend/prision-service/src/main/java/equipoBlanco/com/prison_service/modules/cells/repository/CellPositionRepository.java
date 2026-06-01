package equipoBlanco.com.prison_service.modules.cells.repository;

import equipoBlanco.com.prison_service.modules.cells.model.CellPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CellPositionRepository extends JpaRepository<CellPosition, UUID> {
    Optional<CellPosition> findByCellId(UUID cellId);
    List<CellPosition> findByPrisonMapFloorNumber(Integer floorNumber);

    @Modifying
    @Query("delete from CellPosition cp where cp.cell.id = :cellId")
    void deleteByCellId(UUID cellId);
}
