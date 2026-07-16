package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CalendarioUpdateDto {
    private LocalDate nuevaFecha;
    private String oficialQueRegistro;
    private String observaciones;
}
