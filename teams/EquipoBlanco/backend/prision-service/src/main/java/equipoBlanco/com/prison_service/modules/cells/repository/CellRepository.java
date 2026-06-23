package equipoBlanco.com.prison_service.modules.cells.repository;

import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CellRepository extends JpaRepository<Cell, UUID> {
    Optional<Cell> findByIdentifier(String identifier);
    boolean existsByIdentifier(String identifier);

    @Query("select c from Cell c where exists (select 1 from CellPosition cp where cp.cell.id = c.id)")
    List<Cell> findPlacedCells();
}
