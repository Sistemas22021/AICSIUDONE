package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.Belonging;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BelongingRepository extends JpaRepository<Belonging, UUID> {
}
