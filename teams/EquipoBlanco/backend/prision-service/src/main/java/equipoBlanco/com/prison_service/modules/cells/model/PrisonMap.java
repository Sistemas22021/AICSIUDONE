package equipoBlanco.com.prison_service.modules.cells.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prison_maps")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PrisonMap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer floorNumber;

    @Column(columnDefinition = "TEXT")
    private String backgroundImage;

    @OneToMany(mappedBy = "prisonMap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CellPosition> positions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static PrisonMap ofFloor(Integer floorNumber) {
        PrisonMap map = new PrisonMap();
        map.floorNumber = floorNumber;
        return map;
    }
}
