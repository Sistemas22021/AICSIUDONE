package com.guardia.core.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.guardia.core.model.Escena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repositorio JPA para escenas de levantamiento.
 * Incluye consultas por expediente, investigador y una query fetch para investigador.
 */
public interface EscenaRepository extends JpaRepository<Escena, Long> {
    List<Escena> findByExpedienteId(Long expedienteId);
    List<Escena> findByLevantadaPorId(Long usuarioId);
    List<Escena> findByEstadoChecklist(String estado);
    @Query("SELECT e FROM Escena e LEFT JOIN FETCH e.levantadaPor WHERE e.id = :id")
    Optional<Escena> findByIdWithInvestigador(@Param("id") Long id);
}

