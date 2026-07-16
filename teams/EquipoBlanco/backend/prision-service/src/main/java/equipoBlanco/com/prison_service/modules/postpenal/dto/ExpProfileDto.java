package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpProfileDto {
    private String domicilio;
    private String municipio;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String nivelRiesgo;
}
