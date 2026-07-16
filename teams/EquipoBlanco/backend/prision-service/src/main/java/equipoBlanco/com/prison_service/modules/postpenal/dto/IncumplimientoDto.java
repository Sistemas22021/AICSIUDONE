package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IncumplimientoDto {
    private String oficialQueRegistro;
    private String observaciones;
    private LocalDateTime fechaDetectada; // Fecha y hora en que se detectó el incumplimiento
}
