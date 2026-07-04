package equipoBlanco.com.prison_service.modules.control.repository;

import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AlertaRepository extends JpaRepository<Alerta, UUID> {

    /** Para la campana: alertas activas del usuario logueado */
    List<Alerta> findByDestinatarioAndEstado(String destinatario, String estado);

    /** Para el dashboard: todas las alertas de nivel 1 (activas e históricas) */
    List<Alerta> findByNivel(Integer nivel);

    /** Para filtrar alertas de nivel 1 que siguen activas */
    List<Alerta> findByNivelAndEstado(Integer nivel, String estado);
}

