package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
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
}
