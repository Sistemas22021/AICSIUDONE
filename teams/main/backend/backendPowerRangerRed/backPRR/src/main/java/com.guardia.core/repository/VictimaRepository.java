package com.guardia.core.repository;

import com.guardia.core.model.Victima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VictimaRepository extends JpaRepository<Victima, Long> {
    List<Victima> findByExpedienteId(Long expedienteId);
    Optional<Victima> findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
}
