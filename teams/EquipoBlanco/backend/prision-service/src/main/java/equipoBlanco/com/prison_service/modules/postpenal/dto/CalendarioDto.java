package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class CalendarioDto {
    private UUID id;
    private UUID expedienteId;
    private LocalDate fechaProgramada;
    private String estado;
    private String frecuencia;
    private LocalDate fechaInicio;
    private String oficialQueRegistro;
    private String observaciones;
    private String reclusoNombre;
    private String reclusoCedula;

    // HU-S3-03
    private LocalDate fechaReal;
    private LocalTime horaReal;

    // HU-S3-05
    private Boolean detectadoPorSistema;
    private LocalDateTime fechaIncumplimiento;
}
