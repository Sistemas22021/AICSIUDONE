package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DischargeDto {
    private String motivoEgreso;
    private LocalDate fechaEgreso;
    private String observacionesEgreso;
}
