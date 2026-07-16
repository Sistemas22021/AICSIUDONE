package equipoBlanco.com.prison_service.modules.inmates.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "belonging_handovers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BelongingHandover {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    @Column(nullable = false)
    private LocalDateTime handoverDate;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientCedula;

    @Column(nullable = false)
    private String authorizedBy; // Supervisor who authorized the handover
}
