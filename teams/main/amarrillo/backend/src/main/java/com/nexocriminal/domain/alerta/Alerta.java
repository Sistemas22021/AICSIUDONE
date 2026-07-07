package com.nexocriminal.domain.alerta;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoAlerta tipo;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_riesgo", length = 10)
    private NivelRiesgo nivelRiesgo;

    @Column(name = "vinculo_id")
    private Long vinculoId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EstadoAlerta estado = EstadoAlerta.PENDIENTE;

    @Column(name = "creada_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadaEn = LocalDateTime.now();
}
