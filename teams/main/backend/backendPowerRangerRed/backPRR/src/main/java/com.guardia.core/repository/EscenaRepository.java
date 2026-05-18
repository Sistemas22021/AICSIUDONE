package com.guardia.core.repository;

import com.guardia.core.model.Escena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscenaRepository extends JpaRepository<Escena, Long> {
    List<Escena> findByExpedienteId(Long expedienteId);
    List<Escena> findByLevantadaPorId(Long usuarioId);
    List<Escena> findByEstadoChecklist(String estado);
}
