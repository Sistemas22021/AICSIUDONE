package equipoBlanco.com.prison_service.modules.cells.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "cell_positions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CellPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cell_id", nullable = false, unique = true)
    private Cell cell;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prison_map_id", nullable = false)
    private PrisonMap prisonMap;

    @Column(nullable = false)
    private Double posX;

    @Column(nullable = false)
    private Double posY;

    @Column(nullable = false)
    @Builder.Default
    private Integer radius = 18;
}
