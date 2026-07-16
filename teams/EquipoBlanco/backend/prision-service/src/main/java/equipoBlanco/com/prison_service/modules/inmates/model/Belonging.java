package equipoBlanco.com.prison_service.modules.inmates.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "belongings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Belonging {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    private String description;
    private Integer quantity;
    @Column(columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BelongingStatus status = BelongingStatus.ALMACENADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handover_id")
    private BelongingHandover handover;

    public enum BelongingStatus {
        ALMACENADO, ENTREGADO, RETENIDO_INVESTIGACION
    }
}
