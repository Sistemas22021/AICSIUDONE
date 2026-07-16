package equipoBlanco.com.prison_service.modules.inmates.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TemporaryEgressDto {
    private String motivoSalidaTemporal;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime fechaSalidaTemporal;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime fechaRetornoEstimada;

    private String observaciones;
}
