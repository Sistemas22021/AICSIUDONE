package equipoBlanco.com.prison_service.modules.inmates.service;

import equipoBlanco.com.prison_service.modules.inmates.dto.InmateDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Belonging;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InmateService {

    private final InmateRepository inmateRepository;

    public boolean cedulaHasActiveRecord(String cedula) {
        return inmateRepository.existsByCedulaAndStatusNot(cedula, InmateStatus.EGRESADO);
    }

    public InmateDto register(InmateDto dto) {
        if (cedulaHasActiveRecord(dto.getCedula())) {
            throw new RuntimeException("Ya existe un expediente activo con la cédula: " + dto.getCedula());
        }

        LocalDate release = dto.getAdmissionDate()
            .plusYears(dto.getSentenceYears() != null ? dto.getSentenceYears() : 0)
            .plusMonths(dto.getSentenceMonths() != null ? dto.getSentenceMonths() : 0);

        Inmate inmate = Inmate.builder()
            .cedula(dto.getCedula())
            .firstName(dto.getFirstName())
            .secondName(dto.getSecondName())
            .firstLastname(dto.getFirstLastname())
            .secondLastname(dto.getSecondLastname())
            .birthDate(dto.getBirthDate())
            .crime(dto.getCrime())
            .caseNumber(dto.getCaseNumber())
            .court(dto.getCourt())
            .admissionDate(dto.getAdmissionDate())
            .sentenceYears(dto.getSentenceYears())
            .sentenceMonths(dto.getSentenceMonths())
            .estimatedReleaseDate(release)
            .eyeColor(dto.getEyeColor())
            .hairColor(dto.getHairColor())
            .bodyBuild(dto.getBodyBuild())
            .heightCm(dto.getHeightCm())
            .weightKg(dto.getWeightKg())
            .distinguishingMarks(dto.getDistinguishingMarks())
            .photoUrl(dto.getPhotoUrl())
            .fingerprintUrl(dto.getFingerprintUrl())
            .status(InmateStatus.ACTIVO_SIN_CELDA)
            .build();

        if (dto.getBelongings() != null) {
            List<Belonging> belongings = dto.getBelongings().stream().map(b ->
                Belonging.builder()
                    .inmate(inmate)
                    .description(b.getDescription())
                    .quantity(b.getQuantity())
                    .observations(b.getObservations())
                    .build()
            ).toList();
            inmate.setBelongings(belongings);
        }

        return toDto(inmateRepository.save(inmate));
    }

    public List<InmateDto> getByStatus(InmateStatus status) {
        return inmateRepository.findByStatus(status).stream()
            .map(this::toDto).toList();
    }

    private InmateDto toDto(Inmate i) {
        return InmateDto.builder()
            .id(i.getId())
            .cedula(i.getCedula())
            .firstName(i.getFirstName())
            .firstLastname(i.getFirstLastname())
            .status(i.getStatus())
            .estimatedReleaseDate(i.getEstimatedReleaseDate())
            .build();
    }
}
