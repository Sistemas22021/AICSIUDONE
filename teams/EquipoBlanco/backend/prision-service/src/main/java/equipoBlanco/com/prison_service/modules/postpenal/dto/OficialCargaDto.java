package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OficialCargaDto {
    private String nombre;
    private String cedula;
    private long casosActivos;
}
