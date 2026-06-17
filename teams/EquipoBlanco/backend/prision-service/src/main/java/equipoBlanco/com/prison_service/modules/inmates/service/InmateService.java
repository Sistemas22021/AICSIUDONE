package equipoBlanco.com.prison_service.modules.inmates.service;

import equipoBlanco.com.prison_service.modules.inmates.dto.InmateDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.BelongingDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Belonging;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.model.InmatePhoto;
import equipoBlanco.com.prison_service.modules.inmates.model.InmateFingerprint;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import equipoBlanco.com.prison_service.modules.inmates.dto.DischargeDto;
import equipoBlanco.com.prison_service.modules.postpenal.service.PostPenalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InmateService {

    private final InmateRepository inmateRepository;
    private final PostPenalService postPenalService;

    public boolean cedulaHasActiveRecord(String cedula) {
        return inmateRepository.existsByCedulaAndStatusNot(cedula, InmateStatus.EGRESADO);
    }

    public InmateDto register(InmateDto dto) {
        if (cedulaHasActiveRecord(dto.getCedula())) {
            throw new RuntimeException("Ya existe un expediente activo con la cédula: " + dto.getCedula());
        }

        LocalDate release = null;
        if (dto.getAdmissionDate() != null) {
            release = dto.getAdmissionDate()
                .plusYears(dto.getSentenceYears() != null ? dto.getSentenceYears() : 0)
                .plusMonths(dto.getSentenceMonths() != null ? dto.getSentenceMonths() : 0);
        }


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
            .status(InmateStatus.ACTIVO_SIN_CELDA)
            .build();

        java.util.List<InmatePhoto> photos = new java.util.ArrayList<>();
        if (dto.getPhotoUrl() != null && !dto.getPhotoUrl().trim().isEmpty()) {
            photos.add(InmatePhoto.builder().inmate(inmate).url(dto.getPhotoUrl()).description("Foto Frontal").build());
        }
        if (dto.getPhotoUrl2() != null && !dto.getPhotoUrl2().trim().isEmpty()) {
            photos.add(InmatePhoto.builder().inmate(inmate).url(dto.getPhotoUrl2()).description("Perfil Izquierdo").build());
        }
        if (dto.getPhotoUrl3() != null && !dto.getPhotoUrl3().trim().isEmpty()) {
            photos.add(InmatePhoto.builder().inmate(inmate).url(dto.getPhotoUrl3()).description("Perfil Derecho").build());
        }
        inmate.setPhotos(photos);

        java.util.List<InmateFingerprint> fingerprints = new java.util.ArrayList<>();
        if (dto.getFingerprintUrl() != null && !dto.getFingerprintUrl().trim().isEmpty()) {
            fingerprints.add(InmateFingerprint.builder().inmate(inmate).url(dto.getFingerprintUrl()).description("Mano Izquierda").build());
        }
        if (dto.getFingerprintRightUrl() != null && !dto.getFingerprintRightUrl().trim().isEmpty()) {
            fingerprints.add(InmateFingerprint.builder().inmate(inmate).url(dto.getFingerprintRightUrl()).description("Mano Derecha").build());
        }
        inmate.setFingerprints(fingerprints);

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

    public List<InmateDto> getAllInmates() {
        return inmateRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public InmateDto getInmateById(UUID id) {
        Inmate inmate = inmateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado con ID: " + id));
        return toDetailedDto(inmate);
    }

    public InmateDto getInmateByCedula(String cedula) {
        Inmate inmate = inmateRepository.findByCedula(cedula)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado con Cédula: " + cedula));
        return toDetailedDto(inmate);
    }

    public List<InmateDto> getInmatesByCell(UUID cellId) {
        return inmateRepository.findAll().stream()
            .filter(i -> i.getCell() != null && i.getCell().getId().equals(cellId))
            .map(this::toDto)
            .toList();
    }

    public InmateDto dischargeInmate(UUID inmateId, DischargeDto dto) {
        Inmate inmate = inmateRepository.findById(inmateId)
            .orElseThrow(() -> new RuntimeException("Recluso no encontrado con ID: " + inmateId));

        if (inmate.getStatus() == InmateStatus.EGRESADO) {
            throw new RuntimeException("El recluso ya se encuentra egresado");
        }

        inmate.setStatus(InmateStatus.EGRESADO);
        inmate.setCell(null);
        inmate.setDischargeDate(dto.getFechaEgreso() != null ? dto.getFechaEgreso() : LocalDate.now());
        inmate.setMotivoEgreso(dto.getMotivoEgreso());
        inmate.setObservacionesEgreso(dto.getObservacionesEgreso());

        if ("Cumplimiento de condena".equalsIgnoreCase(dto.getMotivoEgreso()) ||
            "Libertad condicional".equalsIgnoreCase(dto.getMotivoEgreso())) {
            postPenalService.createBaseProfile(inmate, inmate.getDischargeDate());
        }

        return toDto(inmateRepository.save(inmate));
    }

    private InmateDto toDto(Inmate i) {
        return InmateDto.builder()
            .id(i.getId())
            .cedula(i.getCedula())
            .firstName(i.getFirstName())
            .firstLastname(i.getFirstLastname())
            .status(i.getStatus())
            .admissionDate(i.getAdmissionDate())
            .dischargeDate(i.getDischargeDate())
            .motivoEgreso(i.getMotivoEgreso())
            .observacionesEgreso(i.getObservacionesEgreso())
            .estimatedReleaseDate(i.getEstimatedReleaseDate())
            .cellId(i.getCell() != null ? i.getCell().getId() : null)
            .cellIdentifier(i.getCell() != null ? i.getCell().getIdentifier() : null)
            .build();
    }

    private InmateDto toDetailedDto(Inmate i) {
        List<BelongingDto> belongingDtos = null;
        if (i.getBelongings() != null) {
            belongingDtos = i.getBelongings().stream().map(b ->
                BelongingDto.builder()
                    .id(b.getId())
                    .description(b.getDescription())
                    .quantity(b.getQuantity())
                    .observations(b.getObservations())
                    .build()
            ).toList();
        }

        String photoUrl = null;
        String photoUrl2 = null;
        String photoUrl3 = null;
        if (i.getPhotos() != null) {
            for (InmatePhoto photo : i.getPhotos()) {
                if ("Foto Frontal".equals(photo.getDescription())) {
                    photoUrl = photo.getUrl();
                } else if ("Perfil Izquierdo".equals(photo.getDescription())) {
                    photoUrl2 = photo.getUrl();
                } else if ("Perfil Derecho".equals(photo.getDescription())) {
                    photoUrl3 = photo.getUrl();
                }
            }
        }

        String fingerprintUrl = null;
        String fingerprintRightUrl = null;
        if (i.getFingerprints() != null) {
            for (InmateFingerprint fingerprint : i.getFingerprints()) {
                if ("Mano Izquierda".equals(fingerprint.getDescription())) {
                    fingerprintUrl = fingerprint.getUrl();
                } else if ("Mano Derecha".equals(fingerprint.getDescription())) {
                    fingerprintRightUrl = fingerprint.getUrl();
                }
            }
        }

        return InmateDto.builder()
            .id(i.getId())
            .cedula(i.getCedula())
            .firstName(i.getFirstName())
            .secondName(i.getSecondName())
            .firstLastname(i.getFirstLastname())
            .secondLastname(i.getSecondLastname())
            .birthDate(i.getBirthDate())
            .crime(i.getCrime())
            .caseNumber(i.getCaseNumber())
            .court(i.getCourt())
            .admissionDate(i.getAdmissionDate())
            .dischargeDate(i.getDischargeDate())
            .motivoEgreso(i.getMotivoEgreso())
            .observacionesEgreso(i.getObservacionesEgreso())
            .sentenceYears(i.getSentenceYears())
            .sentenceMonths(i.getSentenceMonths())
            .estimatedReleaseDate(i.getEstimatedReleaseDate())
            .eyeColor(i.getEyeColor())
            .hairColor(i.getHairColor())
            .bodyBuild(i.getBodyBuild())
            .heightCm(i.getHeightCm())
            .weightKg(i.getWeightKg())
            .distinguishingMarks(i.getDistinguishingMarks())
            .photoUrl(photoUrl)
            .photoUrl2(photoUrl2)
            .photoUrl3(photoUrl3)
            .fingerprintUrl(fingerprintUrl)
            .fingerprintRightUrl(fingerprintRightUrl)
            .status(i.getStatus())
            .belongings(belongingDtos)
            .cellId(i.getCell() != null ? i.getCell().getId() : null)
            .cellIdentifier(i.getCell() != null ? i.getCell().getIdentifier() : null)
            .build();
    }
}
