package com.guardia.core.model;

import com.guardia.core.model.enums.PasoChecklist;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "firma_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FirmaChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escena_checklist_id", nullable = false)
    private EscenaChecklist pasoChecklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigador_id", nullable = false)
    private Usuario investigador;

    @Enumerated(EnumType.STRING)
    @Column(name = "paso", nullable = false)
    private PasoChecklist paso;

    @Column(name = "timestamp_firma", nullable = false, updatable = false)
    private LocalDateTime timestampFirma;

    @Column(name = "exitoso", nullable = false)
    private Boolean exitoso;

    // Registra intentos fallidos sin bloquear el flujo
    @Column(name = "motivo_fallo")
    private String motivoFallo;

    public String getMotivoFallo() { return this.motivoFallo; }
}