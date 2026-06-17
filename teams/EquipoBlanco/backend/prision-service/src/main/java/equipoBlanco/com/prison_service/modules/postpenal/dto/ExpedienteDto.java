package equipoBlanco.com.prison_service.modules.postpenal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ExpedienteDto {
    private UUID id;
    private UUID idRecluso;
    private String reclusoNombre;
    private String reclusoCedula;
    private LocalDate fechaEgreso;
    private String oficialAsignadoNombre;
    private String oficialAsignadoCedula;
    private String estado;
    private List<String> historialAsignaciones;
}
