package com.nexocriminal.domain.avistamiento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.vehiculo.Vehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "avistamiento")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehiculo vehiculo;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ubicacion ubicacion;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 50)
    private String fuente;

    @Column(name = "creado_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}