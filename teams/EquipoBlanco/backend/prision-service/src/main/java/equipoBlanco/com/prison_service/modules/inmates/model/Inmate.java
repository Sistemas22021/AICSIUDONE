package equipoBlanco.com.prison_service.modules.inmates.model;

import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inmates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inmate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false) private String firstName;
    private String secondName;
    @Column(nullable = false) private String firstLastname;
    private String secondLastname;
    private LocalDate birthDate;

    private String crime;
    private String caseNumber;
    private String court;

    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private Integer sentenceYears;
    private Integer sentenceMonths;
    private LocalDate estimatedReleaseDate;

    private String eyeColor;
    private String hairColor;
    private String bodyBuild;
    private Integer heightCm;
    private BigDecimal weightKg;
    @Column(columnDefinition = "TEXT")
    private String distinguishingMarks;

    @OneToMany(mappedBy = "inmate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InmatePhoto> photos;

    @OneToMany(mappedBy = "inmate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InmateFingerprint> fingerprints;

    @Enumerated(EnumType.STRING)
    private InmateStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cell_id")
    private Cell cell;

    @OneToMany(mappedBy = "inmate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Belonging> belongings;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = InmateStatus.ACTIVO_SIN_CELDA;
    }

    public enum InmateStatus {
        ACTIVO_SIN_CELDA, ACTIVO_CON_CELDA, EGRESADO
    }
}
