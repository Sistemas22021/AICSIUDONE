package equipoBlanco.com.prison_service.modules.cells.model;

import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cell_assignments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CellAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cell_id", nullable = false)
    private Cell cell;

    @Column(nullable = false)
    private String assignedBy;

    @Column(nullable = false)
    private LocalDateTime assignedAt;
}
