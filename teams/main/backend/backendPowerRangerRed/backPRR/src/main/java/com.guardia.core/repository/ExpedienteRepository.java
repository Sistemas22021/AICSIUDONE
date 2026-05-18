package com.guardia.core.repository;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.enums.EstadoExpediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
    Optional<Expediente> findByFolio(String folio);
    List<Expediente> findByEstadoExpediente(EstadoExpediente estado);
    List<Expediente> findByCreadoPorId(Long usuarioId);
    List<Expediente> findBySelladoPorId(Long usuarioId);
    List<Expediente> findByTipoDelitoId(Long tipoDelitoId);
    boolean existsByFolio(String folio);

    @Query("SELECT e FROM Expediente e WHERE e.tipoDelito.id = :tipoId AND e.estadoExpediente = :estado")
    List<Expediente> findByTipoDelitoAndEstado(@Param("tipoId") Long tipoId,
                                               @Param("estado") EstadoExpediente estado);
}
