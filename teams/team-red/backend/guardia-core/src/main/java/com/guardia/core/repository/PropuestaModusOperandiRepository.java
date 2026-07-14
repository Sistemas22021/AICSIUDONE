package com.guardia.core.repository;

import com.guardia.core.model.PropuestaModusOperandi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repositorio JPA para propuestas de Modus Operandi (HU2/HU3).
 */
public interface PropuestaModusOperandiRepository extends JpaRepository<PropuestaModusOperandi, Long> {

    Optional<PropuestaModusOperandi> findByExpedienteIdAndVigenteTrue(Long expedienteId);

    /** Historial completo (vigente + versiones anteriores), más reciente primero. HU3 CA5. */
    List<PropuestaModusOperandi> findByExpedienteIdOrderByVersionDesc(Long expedienteId);
}