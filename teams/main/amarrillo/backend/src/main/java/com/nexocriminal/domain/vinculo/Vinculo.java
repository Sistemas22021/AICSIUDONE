package com.nexocriminal.domain.vinculo;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vinculo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Vinculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_origen", nullable = false, length = 20)
    private String tipoOrigen;

    @Column(name = "id_origen", nullable = false)
    private Long idOrigen;

    @Column(name = "tipo_destino", nullable = false, length = 20)
    private String tipoDestino;

    @Column(name = "id_destino", nullable = false)
    private Long idDestino;

    @Column(name = "regla_detectada", nullable = false, length = 50)
    private String reglaDetectada;

    @Column(nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal score = BigDecimal.valueOf(1.0);

    @Builder.Default
    private Boolean activo = true;

    @Column(name = "detectado_en", updatable = false)
    @Builder.Default
    private LocalDateTime detectadoEn = LocalDateTime.now();
}
