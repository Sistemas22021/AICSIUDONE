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
    private String status; // Ej: EN_INVESTIGACION

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentParticipant> participants;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
