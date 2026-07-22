package com.nexocriminal.domain.desaparecida;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Una foto asociada a una persona desaparecida.
 * Una persona puede tener multiples fotos (OneToMany).
 */
@Entity
@Table(name = "foto_desaparecida")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FotoDesaparecida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** URL relativa servida por el backend, ej: /files/desaparecidas/uuid.jpg */
    @Column(nullable = false, length = 500)
    private String url;

    /** Orden de visualizacion (0 = primera) */
    @Column(nullable = false)
    private Integer orden;

    /** Si es la foto principal/destacada */
    @Column(nullable = false)
    private Boolean principal;

    /** Descripcion opcional de la foto */
    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_desaparecida_id")
    @JsonIgnoreProperties({"fotos", "hibernateLazyInitializer", "handler"})
    private PersonaDesaparecida personaDesaparecida;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
        if (orden == null) orden = 0;
        if (principal == null) principal = false;
    }
}