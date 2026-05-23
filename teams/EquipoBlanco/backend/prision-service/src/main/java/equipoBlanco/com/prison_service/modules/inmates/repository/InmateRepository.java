package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InmateRepository extends JpaRepository<Inmate, UUID> {
    Optional<Inmate> findByCedulaAndStatusNot(String cedula, InmateStatus status);
    boolean existsByCedulaAndStatusNot(String cedula, InmateStatus status);

    @Query("SELECT COUNT(i) FROM Inmate i WHERE i.cell.id = :cellId AND i.status = 'ACTIVO_CON_CELDA'")
    int countByCellId(UUID cellId);

    List<Inmate> findByStatus(InmateStatus status);
}
