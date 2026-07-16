package equipoBlanco.com.prison_service.modules.postpenal.scheduler;

import equipoBlanco.com.prison_service.modules.postpenal.service.CalendarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarioScheduler {

    private final CalendarioService calendarioService;

    @Scheduled(cron = "${app.scheduler.presentaciones-vencidas.cron:0 59 23 * * *}")
    public void ejecutarDeteccionAutomatica() {
        log.info("Iniciando detección automática de presentaciones vencidas...");
        try {
            calendarioService.procesarPresentacionesVencidas();
            log.info("Detección automática de presentaciones vencidas completada exitosamente.");
        } catch (Exception e) {
            log.error("Error al procesar presentaciones vencidas automáticamente: ", e);
        }
    }
}
