package equipoBlanco.com.prison_service.modules.postpenal.repository;

import equipoBlanco.com.prison_service.modules.postpenal.model.CalendarioPresentacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarioPresentacionRepository extends JpaRepository<CalendarioPresentacion, UUID> {
    List<CalendarioPresentacion> findByExpedienteIdOrderByFechaProgramadaAsc(UUID expedienteId);
    List<CalendarioPresentacion> findByFechaProgramadaAndEstado(LocalDate fechaProgramada, String estado);
}
