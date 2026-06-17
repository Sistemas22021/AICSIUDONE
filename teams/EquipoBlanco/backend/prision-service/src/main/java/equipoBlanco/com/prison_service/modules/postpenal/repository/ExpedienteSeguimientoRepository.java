package equipoBlanco.com.prison_service.modules.postpenal.repository;

import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpedienteSeguimientoRepository extends JpaRepository<ExpedienteSeguimiento, UUID> {
    Optional<ExpedienteSeguimiento> findByIdRecluso(UUID idRecluso);

    List<ExpedienteSeguimiento> findByEstadoOrderByFechaEgresoAsc(String estado);

    List<ExpedienteSeguimiento> findByEstado(String estado);

    @Query("SELECT e.oficialAsignadoCedula, e.oficialAsignadoNombre, COUNT(e) FROM ExpedienteSeguimiento e WHERE e.estado = 'asignado' AND e.oficialAsignadoCedula IS NOT NULL GROUP BY e.oficialAsignadoCedula, e.oficialAsignadoNombre")
    List<Object[]> countCasosActivosByOficial();
}
