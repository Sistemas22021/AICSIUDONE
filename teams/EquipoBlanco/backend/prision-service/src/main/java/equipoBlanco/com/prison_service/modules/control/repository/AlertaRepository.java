package equipoBlanco.com.prison_service.modules.control.repository;

import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AlertaRepository extends JpaRepository<Alerta, UUID> {
}
