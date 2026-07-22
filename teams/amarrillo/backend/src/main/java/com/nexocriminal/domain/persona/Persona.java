package com.nexocriminal.domain.persona;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nexocriminal.validacion.anotaciones.CedulaVenezolana;
import com.nexocriminal.validacion.anotaciones.TelefonoVenezolano;

@Entity
@Table(name = "persona")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @CedulaVenezolana
    @Column(unique = true, nullable = false, length = 20)
    private String documento;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 100)
    private String alias;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolPersona rol;

    @TelefonoVenezolano
    @Column(length = 20)
    private String telefono;

    @Column(length = 20)
    @Builder.Default
    private String estado = "ACTIVO";

    @Column(name = "creado_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "actualizado_en")
    @Builder.Default
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
