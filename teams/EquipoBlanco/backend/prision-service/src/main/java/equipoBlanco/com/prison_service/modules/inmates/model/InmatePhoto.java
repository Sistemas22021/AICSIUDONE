package equipoBlanco.com.prison_service.modules.inmates.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inmate_photos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InmatePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
