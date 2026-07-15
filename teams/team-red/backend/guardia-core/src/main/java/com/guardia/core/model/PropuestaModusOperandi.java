package com.guardia.core.model;

import com.guardia.core.model.enums.EstadoPropuestaMO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Propuesta de Modus Operandi (MO) generada automáticamente por el pipeline de IA
 * de la HU2, y objeto central de la validación experta de la HU3.
 *
 * <p>Cada expediente puede tener varias propuestas a lo largo del tiempo (una por
 * cada re-análisis); solo una está marcada como {@code vigente = true} en un
 * momento dado. El historial completo (vigentes y no vigentes) se conserva para
 * trazabilidad (HU3, CA5: "El historial de cambios del MO ... es visible para el
 * Guardia y el Investigador asignado").</p>
 */
@Entity
@Table(name = "propuestas_modus_operandi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropuestaModusOperandi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    /** Número de versión dentro del historial de MO del expediente (1, 2, 3...). */
    @Column(nullable = false)
    private Integer version;

    /** Solo una propuesta por expediente puede estar vigente a la vez. */
    @Column(nullable = false)
    @Builder.Default
    private boolean vigente = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPropuestaMO estado;

    @Column(name = "caracteristicas_comunes", columnDefinition = "TEXT")
    private String caracteristicasComunes;

    @Column(name = "posible_firma", columnDefinition = "TEXT")
    private String posibleFirma;

    @Column(name = "consistencia_horario_zona", columnDefinition = "TEXT")
    private String consistenciaHorarioZona;

    @Column(name = "resumen_generado", columnDefinition = "TEXT")
    private String resumenGenerado;

    @Column(name = "nivel_confianza")
    private Double nivelConfianza;

    @Column(name = "modelo_embedding", length = 80)
    private String modeloEmbedding;

    @Column(name = "modelo_chat", length = 80)
    private String modeloChat;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @ElementCollection
    @CollectionTable(name = "propuesta_mo_expediente_similar", joinColumns = @JoinColumn(name = "propuesta_mo_id"))
    @Builder.Default
    private List<ExpedienteSimilarMO> expedientesSimilares = new ArrayList<>();

    /** Una vez true, el sistema ya no puede sobreescribir esta propuesta automáticamente. */
    @Column(name = "revisado_por_experto", nullable = false)
    @Builder.Default
    private boolean revisadoPorExperto = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analista_revisor_id")
    private Usuario analistaRevisor;

    /** Obligatoria cuando la acción del analista es CORREGIR o RECHAZAR. */
    @Column(name = "justificacion_revision", columnDefinition = "TEXT")
    private String justificacionRevision;

    /** Solo aplica cuando el analista rechaza la propuesta de la IA (HU3, CA2). */
    @Column(name = "clasificacion_manual", columnDefinition = "TEXT")
    private String clasificacionManual;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;
}