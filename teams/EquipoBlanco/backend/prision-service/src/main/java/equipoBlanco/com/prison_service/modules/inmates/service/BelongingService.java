package equipoBlanco.com.prison_service.modules.inmates.service;

import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingHandoverDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Belonging;
import equipoBlanco.com.prison_service.modules.inmates.model.Belonging.BelongingStatus;
import equipoBlanco.com.prison_service.modules.inmates.model.BelongingHandover;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.repository.BelongingHandoverRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.BelongingRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BelongingService {

    private final BelongingRepository belongingRepository;
    private final BelongingHandoverRepository belongingHandoverRepository;
    private final InmateRepository inmateRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public BelongingDto toggleRetainedStatus(UUID belongingId) {
        Belonging belonging = belongingRepository.findById(belongingId)
            .orElseThrow(() -> new RuntimeException("Objeto de pertenencia no encontrado"));

        if (belonging.getStatus() == BelongingStatus.ENTREGADO) {
            throw new RuntimeException("No se puede cambiar el estado de un objeto que ya ha sido entregado");
        }

        if (belonging.getStatus() == BelongingStatus.ALMACENADO) {
            belonging.setStatus(BelongingStatus.RETENIDO_INVESTIGACION);
        } else {
            belonging.setStatus(BelongingStatus.ALMACENADO);
        }

        return toDto(belongingRepository.save(belonging));
    }

    @Transactional
    public BelongingHandoverDto handoverBelongings(UUID inmateId, BelongingHandoverDto dto, String supervisorName) {
        Inmate inmate = inmateRepository.findById(inmateId)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado"));

        if (dto.getBelongingIds() == null || dto.getBelongingIds().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos una pertenencia para entregar");
        }

        // Create handover audit record
        BelongingHandover handover = BelongingHandover.builder()
            .inmate(inmate)
            .handoverDate(dto.getHandoverDate() != null ? dto.getHandoverDate() : LocalDateTime.now())
            .recipientName(dto.getRecipientName())
            .recipientCedula(dto.getRecipientCedula())
            .authorizedBy(supervisorName)
            .build();
        belongingHandoverRepository.save(handover);

        List<String> deliveredDescriptions = new ArrayList<>();

        for (UUID belongingId : dto.getBelongingIds()) {
            Belonging belonging = belongingRepository.findById(belongingId)
                .orElseThrow(() -> new RuntimeException("Objeto de pertenencia no encontrado: " + belongingId));

            if (!belonging.getInmate().getId().equals(inmateId)) {
                throw new RuntimeException("El objeto no pertenece al recluso especificado");
            }

            if (belonging.getStatus() == BelongingStatus.ENTREGADO) {
                throw new RuntimeException("El objeto '" + belonging.getDescription() + "' ya fue entregado anteriormente");
            }

            if (belonging.getStatus() == BelongingStatus.RETENIDO_INVESTIGACION) {
                throw new RuntimeException("El objeto '" + belonging.getDescription() + "' está retenido por investigación criminal y no puede ser entregado");
            }

            belonging.setStatus(BelongingStatus.ENTREGADO);
            belonging.setHandover(handover);
            belongingRepository.save(belonging);

            deliveredDescriptions.add(belonging.getDescription() + " (x" + belonging.getQuantity() + ")");
        }

        // Register in inmate status history
        if (inmate.getStatusHistory() == null) {
            inmate.setStatusHistory(new ArrayList<>());
        }
        String timestamp = LocalDateTime.now().format(FORMATTER);
        inmate.getStatusHistory().add(String.format("[%s] Entrega de pertenencias autorizada por %s. Familiar receptor: %s (C.I. %s). Objetos entregados: %s.",
            timestamp, supervisorName, dto.getRecipientName(), dto.getRecipientCedula(), String.join(", ", deliveredDescriptions)));
        inmateRepository.save(inmate);

        return BelongingHandoverDto.builder()
            .id(handover.getId())
            .inmateId(inmateId)
            .handoverDate(handover.getHandoverDate())
            .recipientName(handover.getRecipientName())
            .recipientCedula(handover.getRecipientCedula())
            .authorizedBy(handover.getAuthorizedBy())
            .belongingIds(dto.getBelongingIds())
            .build();
    }

    public List<BelongingHandoverDto> getHandoversByInmate(UUID inmateId) {
        return belongingHandoverRepository.findByInmateId(inmateId).stream()
            .map(h -> {
                List<UUID> bIds = belongingRepository.findAll().stream()
                    .filter(b -> b.getHandover() != null && b.getHandover().getId().equals(h.getId()))
                    .map(Belonging::getId)
                    .toList();
                return BelongingHandoverDto.builder()
                    .id(h.getId())
                    .inmateId(h.getInmate().getId())
                    .handoverDate(h.getHandoverDate())
                    .recipientName(h.getRecipientName())
                    .recipientCedula(h.getRecipientCedula())
                    .authorizedBy(h.getAuthorizedBy())
                    .belongingIds(bIds)
                    .build();
            })
            .toList();
    }

    private BelongingDto toDto(Belonging b) {
        return BelongingDto.builder()
            .id(b.getId())
            .description(b.getDescription())
            .quantity(b.getQuantity())
            .observations(b.getObservations())
            .status(b.getStatus().name())
            .build();
    }
}
