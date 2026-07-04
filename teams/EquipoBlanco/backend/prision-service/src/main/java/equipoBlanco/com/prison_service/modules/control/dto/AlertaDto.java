package equipoBlanco.com.prison_service.modules.control.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertaDto {

    private UUID id;
    private Integer nivel;
    private LocalDateTime fechaEmision;
    private String destinatario;
    private String estado;
    private String accionRequerida;

    // Datos enriquecidos para la campana y el dashboard
    private UUID expedienteId;
    private String nombreEgresado;
    private String cedulaEgresado;
    private String observacionAtencion;
}
