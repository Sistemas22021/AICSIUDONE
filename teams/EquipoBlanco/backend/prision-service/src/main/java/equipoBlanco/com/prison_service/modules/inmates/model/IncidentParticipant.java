package equipoBlanco.com.prison_service.modules.inmates.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "incident_participants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IncidentParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private InternalIncident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    @Column(nullable = false)
    private String role; // FALLECIDO, COHABITANTE

    @Column(nullable = false)
    private String initialStatus; // FALLECIDO, ILESO, LESIONADO, TRASLADADO_ENFERMERIA

    @Column(columnDefinition = "TEXT")
    private String comments;
}
