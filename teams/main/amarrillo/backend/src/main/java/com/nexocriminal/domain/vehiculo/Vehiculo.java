package com.nexocriminal.domain.vehiculo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexocriminal.domain.persona.Persona;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

import com.nexocriminal.validacion.anotaciones.PlacaVenezolana;


@Entity
@Table(name = "vehiculo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @PlacaVenezolana
    @Column(unique = true, nullable = false, length = 20)
    private String placa;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String marca;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    @Column(length = 40)
    private String color;

    @Column(name = "chasis", length = 40)
    private String chasis;

    @Column(name = "declaracion", columnDefinition = "TEXT")
    private String declaracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoVehiculo estado = EstadoVehiculo.NORMAL;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "propietario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Persona propietario;

    @Column(name = "creado_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}