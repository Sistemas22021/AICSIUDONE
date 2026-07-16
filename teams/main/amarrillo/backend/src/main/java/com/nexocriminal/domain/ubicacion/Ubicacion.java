package com.nexocriminal.domain.ubicacion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String direccion;

    @NotNull
    @Column(nullable = false)
    private Double latitud;

    @NotNull
    @Column(nullable = false)
    private Double longitud;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoUbicacion tipo;

    @Column(name = "nodo_sospechoso")
    @Builder.Default
    private Boolean nodoSospechoso = false;

    @Column(name = "creado_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}