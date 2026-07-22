package com.nexocriminal.domain.relacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexocriminal.domain.persona.Persona;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "relacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Relacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_a_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Persona personaA;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_b_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Persona personaB;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relacion", nullable = false, length = 30)
    private TipoRelacion tipoRelacion;

    @Builder.Default
    private Integer peso = 1;

    @Column(name = "creado_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}