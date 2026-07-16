package com.nexocriminal.testigo;

import jakarta.persistence.*;
import lombok.*;

/**
 * Relación entre un suceso y una persona que fue testigo de él.
 * Tabla intermedia simple (suceso_id + persona_id). Entidad JPA directa,
 * fuera del esquema Clean, igual que modus_operandi y configuracion_motor.
 */
@Entity
@Table(name = "suceso_testigo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"suceso_id", "persona_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SucesoTestigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id del suceso del que esta persona fue testigo. */
    @Column(name = "suceso_id", nullable = false)
    private Long sucesoId;

    /** Id de la persona testigo. */
    @Column(name = "persona_id", nullable = false)
    private Long personaId;
}