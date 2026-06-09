package equipoBlanco.com.prison_service.modules.inmates.service;

import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import equipoBlanco.com.prison_service.modules.cells.model.CellAssignment;
import equipoBlanco.com.prison_service.modules.cells.repository.CellAssignmentRepository;
import equipoBlanco.com.prison_service.modules.cells.repository.CellRepository;
import equipoBlanco.com.prison_service.modules.inmates.dto.TransferRequestDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.model.TransferRequest;
import equipoBlanco.com.prison_service.modules.inmates.model.TransferRequest.TransferStatus;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.TransferRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TransferRequestService {

    private final TransferRequestRepository transferRequestRepository;
    private final InmateRepository inmateRepository;
    private final CellRepository cellRepository;
    private final CellAssignmentRepository cellAssignmentRepository;

    @Transactional
    public TransferRequestDto createRequest(TransferRequestDto dto, String username) {
        Inmate inmate = inmateRepository.findById(dto.getInmateId())
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado"));

        Cell targetCell = cellRepository.findById(dto.getTargetCellId())
            .orElseThrow(() -> new RuntimeException("Celda destino no encontrada"));

        if (inmate.getCell() != null && inmate.getCell().getId().equals(targetCell.getId())) {
            throw new RuntimeException("El recluso ya se encuentra asignado a la celda de destino");
        }

        TransferRequest request = TransferRequest.builder()
            .inmate(inmate)
            .sourceCell(inmate.getCell())
            .targetCell(targetCell)
            .reason(dto.getReason())
            .status(TransferStatus.PENDIENTE)
            .requestedBy(username)
            .build();

        return toDto(transferRequestRepository.save(request));
    }

    @Transactional
    public TransferRequestDto resolveRequest(UUID id, TransferRequestDto resolveDto, String username) {
        TransferRequest request = transferRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud de traslado no encontrada"));

        if (request.getStatus() != TransferStatus.PENDIENTE) {
            throw new RuntimeException("La solicitud ya ha sido resuelta");
        }

        // Segregación de funciones: no puede resolver su propia solicitud
        if (request.getRequestedBy().equalsIgnoreCase(username)) {
            throw new RuntimeException("No puedes resolver tu propia solicitud (Segregación de funciones)");
        }

        TransferStatus newStatus = TransferStatus.valueOf(resolveDto.getStatus().toUpperCase());

        if (newStatus == TransferStatus.RECHAZADO) {
            if (resolveDto.getRejectionReason() == null || resolveDto.getRejectionReason().trim().isEmpty()) {
                throw new RuntimeException("El motivo de rechazo es obligatorio");
            }
            request.setStatus(TransferStatus.RECHAZADO);
            request.setRejectionReason(resolveDto.getRejectionReason());
        } else if (newStatus == TransferStatus.APROBADO) {
            Inmate inmate = request.getInmate();
            Cell targetCell = request.getTargetCell();

            // Traslado del recluso a la nueva celda (capacidad máxima permitida según requerimiento)
            inmate.setCell(targetCell);
            inmate.setStatus(Inmate.InmateStatus.ACTIVO_CON_CELDA);
            inmateRepository.save(inmate);

            // Registro histórico para auditoría
            CellAssignment assignment = CellAssignment.builder()
                .inmate(inmate)
                .cell(targetCell)
                .assignedBy(username)
                .assignedAt(LocalDateTime.now())
                .build();
            cellAssignmentRepository.save(assignment);

            request.setStatus(TransferStatus.APROBADO);
        }

        request.setResolvedBy(username);
        request.setResolvedAt(LocalDateTime.now());

        return toDto(transferRequestRepository.save(request));
    }

    public List<TransferRequestDto> getPendingRequests() {
        return transferRequestRepository.findByStatus(TransferStatus.PENDIENTE).stream()
            .map(this::toDto)
            .toList();
    }

    public List<TransferRequestDto> getInmateHistory(UUID inmateId) {
        return transferRequestRepository.findByInmateIdOrderByCreatedAtDesc(inmateId).stream()
            .map(this::toDto)
            .toList();
    }

    private TransferRequestDto toDto(TransferRequest req) {
        return TransferRequestDto.builder()
            .id(req.getId())
            .inmateId(req.getInmate().getId())
            .inmateName(req.getInmate().getFirstName() + " " + req.getInmate().getFirstLastname())
            .inmateCedula(req.getInmate().getCedula())
            .sourceCellId(req.getSourceCell() != null ? req.getSourceCell().getId() : null)
            .sourceCellIdentifier(req.getSourceCell() != null ? req.getSourceCell().getIdentifier() : "Sin Celda")
            .targetCellId(req.getTargetCell().getId())
            .targetCellIdentifier(req.getTargetCell().getIdentifier())
            .reason(req.getReason())
            .status(req.getStatus().name())
            .requestedBy(req.getRequestedBy())
            .resolvedBy(req.getResolvedBy())
            .createdAt(req.getCreatedAt())
            .resolvedAt(req.getResolvedAt())
            .rejectionReason(req.getRejectionReason())
            .build();
    }
}
