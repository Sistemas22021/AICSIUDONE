package equipoBlanco.com.prison_service.modules.postpenal.repository;

import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ExpedienteSeguimientoRepository extends JpaRepository<ExpedienteSeguimiento, UUID> {
    Optional<ExpedienteSeguimiento> findByIdRecluso(UUID idRecluso);
}
