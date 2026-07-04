package equipoBlanco.com.prison_service.modules.control.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer nivel;
    private LocalDateTime fechaEmision;
    private String destinatario;
    private String estado;
    private String accionRequerida;

    // Campos enriquecidos para HU-S5-02
    private UUID expedienteId;
    private String nombreEgresado;
    private String cedulaEgresado;

    @Column(columnDefinition = "TEXT")
    private String observacionAtencion;

    public void marcarAtendida(String observacion) {
        this.estado = "atendida";
        this.observacionAtencion = observacion;
    }

    /** Compatibilidad retroactiva sin observación */
    public void marcarAtendida() {
        marcarAtendida(null);
    }
}
