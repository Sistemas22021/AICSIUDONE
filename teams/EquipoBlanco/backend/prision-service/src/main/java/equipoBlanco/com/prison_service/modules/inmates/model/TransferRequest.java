package equipoBlanco.com.prison_service.modules.inmates.model;

import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfer_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_cell_id")
    private Cell sourceCell;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_cell_id", nullable = false)
    private Cell targetCell;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(nullable = false)
    private String requestedBy;

    private String resolvedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TransferStatus.PENDIENTE;
        }
    }

    public enum TransferStatus {
        PENDIENTE, APROBADO, RECHAZADO
    }
}
