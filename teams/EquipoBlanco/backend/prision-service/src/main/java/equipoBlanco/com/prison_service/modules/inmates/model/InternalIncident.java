package equipoBlanco.com.prison_service.modules.inmates.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "internal_incidents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InternalIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code; // Ej: INC-2026-0001

    private String cellIdentifier;
    private UUID cellId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime incidentDate;

    @Column(nullable = false)
    private String reporter; // Funcionario que reporta

    @Column(nullable = false)
    private String status; // EN_INVESTIGACION | CERRADO

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentParticipant> participants;

    // ─── Campos de Conclusión de la Investigación ───────────────────────────

    /** HOMICIDIO, SUICIDIO, ACCIDENTAL, NATURAL */
    private String conclusionType;

    /** Causa médica definitiva del fallecimiento */
    @Column(columnDefinition = "TEXT")
    private String causaMedicaDefinitiva;

    /** Número de protocolo de autopsia del instituto forense */
    private String autopsiaProtocolo;

    /** Número de expediente de la Fiscalía */
    private String fiscaliaExpediente;

    /** ID del recluso responsable (solo si aplica — HOMICIDIO) */
    private UUID responsableInmateId;

    /** Nombre del personal responsable (si aplica) */
    private String responsablePersonal;

    /** true si el responsable no aplica (SUICIDIO / ACCIDENTAL) */
    @Builder.Default
    private Boolean responsableNoAplica = false;

    /** Años adicionales de condena imputados al responsable */
    private Integer additionalSentenceYears;

    /** Meses adicionales de condena imputados al responsable */
    private Integer additionalSentenceMonths;

    /** Fecha y hora en que se concluyó la investigación */
    private LocalDateTime concludedAt;

    /** Nombre del funcionario que cerró la investigación */
    private String concludedBy;

    // ────────────────────────────────────────────────────────────────────────

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
