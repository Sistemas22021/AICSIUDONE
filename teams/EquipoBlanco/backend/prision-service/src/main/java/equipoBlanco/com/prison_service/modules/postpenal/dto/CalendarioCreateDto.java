package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CalendarioCreateDto {
    private String frecuencia;
    private LocalDate fechaInicio;
    private String oficialQueRegistro;
}
