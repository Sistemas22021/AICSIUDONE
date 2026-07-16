package com.guardia.core.model;

import com.guardia.core.model.enums.PasoChecklist;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "escena_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un paso del checklist de una escena.
 * Incluye orden, estado de completado y timestamps de inicio/cierre.
 */
public class EscenaChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PasoChecklist paso;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaCierre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escena_id")
    private Escena escena;
}