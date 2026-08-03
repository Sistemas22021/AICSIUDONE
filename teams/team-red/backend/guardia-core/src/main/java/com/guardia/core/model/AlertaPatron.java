package com.guardia.core.model;

import com.guardia.core.model.enums.EstadoAlerta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "alertas_patron")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaPatron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Expediente cuyo análisis de MO disparó la alerta. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_origen_id", nullable = false)
    private Expediente expedienteOrigen;

    /** Propuesta de MO (HU2) que originó esta alerta; trazabilidad hacia el análisis completo. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propuesta_mo_id", nullable = false)
    private PropuestaModusOperandi propuestaOrigen;

    /** Folios y similitud de los expedientes relacionados (CA2). Reutiliza el mismo embeddable que HU2. */
    @ElementCollection
    @CollectionTable(name = "alerta_patron_expediente_relacionado", joinColumns = @JoinColumn(name = "alerta_patron_id"))
    @Builder.Default
    private List<ExpedienteSimilarMO> expedientesRelacionados = new ArrayList<>();

    /** Resumen del patrón en lenguaje natural, redactado por el modelo (CA2). */
    @Column(name = "resumen_patron", columnDefinition = "TEXT", nullable = false)
    private String resumenPatron;

    @Column(name = "nivel_confianza", nullable = false)
    private Double nivelConfianza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoAlerta estado = EstadoAlerta.PENDIENTE;

    /**
     * Clave determinística del conjunto {expediente origen} ∪ {relacionados},
     * usada para detectar duplicados dentro de la ventana de deduplicación
     */
    @Column(name = "clave_deduplicacion", length = 255, nullable = false)
    private String claveDeduplicacion;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    /** Usuarios (Guardia/Investigador) que deben ver esta alerta en su bandeja personal. */
    @ElementCollection
    @CollectionTable(name = "alerta_patron_investigador_notificado", joinColumns = @JoinColumn(name = "alerta_patron_id"))
    @Column(name = "usuario_id")
    @Builder.Default
    private List<UUID> investigadoresNotificados = new ArrayList<>();

    /** Usuario que marcó la alerta como revisada o descartada (CA5). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendida_por_id")
    private Usuario atendidaPor;

    @Column(name = "fecha_atencion")
    private LocalDateTime fechaAtencion;

    /** Motivo opcional capturado al descartar la alerta. */
    @Column(name = "motivo_descarte", columnDefinition = "TEXT")
    private String motivoDescarte;
}