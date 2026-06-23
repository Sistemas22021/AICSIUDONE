package equipoBlanco.com.prison_service.modules.cells.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cells")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cell {

    public static final int MAX_CAPACITY = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private Integer maxCapacity = MAX_CAPACITY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConductLevel conductLevel;

    private BigDecimal lengthMeters;
    private BigDecimal widthMeters;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ConductLevel {
        BAJO, MEDIO, ALTO
    }
}
