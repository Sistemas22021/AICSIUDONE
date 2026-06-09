package equipoBlanco.com.prison_service.modules.inmates.repository;

import equipoBlanco.com.prison_service.modules.inmates.model.TransferRequest;
import equipoBlanco.com.prison_service.modules.inmates.model.TransferRequest.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransferRequestRepository extends JpaRepository<TransferRequest, UUID> {
    List<TransferRequest> findByStatus(TransferStatus status);
    List<TransferRequest> findByInmateIdOrderByCreatedAtDesc(UUID inmateId);
}
