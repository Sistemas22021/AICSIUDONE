package equipoBlanco.com.prison_service.modules.postpenal.service;

import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import equipoBlanco.com.prison_service.modules.postpenal.repository.ExpedienteSeguimientoRepository;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostPenalService {

    private final ExpedienteSeguimientoRepository expedienteSeguimientoRepository;
    private final AlertaRepository alertaRepository;

    public void createBaseProfile(Inmate inmate, LocalDate fechaEgreso) {
        // Crear el expediente
        ExpedienteSeguimiento expediente = ExpedienteSeguimiento.builder()
            .idRecluso(inmate.getId())
            .fechaEgreso(fechaEgreso)
            .estado("pendiente")
            .build();
            
        expedienteSeguimientoRepository.save(expediente);

        // Emitir la alerta
        Alerta alerta = Alerta.builder()
            .nivel(2)
            .fechaEmision(LocalDateTime.now())
            .destinatario("Oficial Policial")
            .estado("activa")
            .accionRequerida("Completar perfil de nuevo egresado (Cédula: " + inmate.getCedula() + ")")
            .build();

        alertaRepository.save(alerta);
    }
}
