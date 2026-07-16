package equipoBlanco.com.prison_service.modules.postpenal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "calendario_presentacion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarioPresentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID expedienteId;

    @Column(nullable = false)
    private LocalDate fechaProgramada;

    @Column(nullable = false)
    private String estado; // PENDIENTE, CUMPLIDA, INCUMPLIDA

    private String frecuencia; // SEMANAL, QUINCENAL, MENSUAL
    
    private LocalDate fechaInicio;

    private String oficialQueRegistro;
    
    @Column(length = 500)
    private String observaciones;

    // HU-S3-03: Campos para registro de cumplimiento
    private LocalDate fechaReal;
    private LocalTime horaReal;

    // HU-S3-05: Campos para registro de incumplimiento
    @Builder.Default
    private Boolean detectadoPorSistema = false;
    private LocalDateTime fechaIncumplimiento;
}
