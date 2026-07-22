package com.azulcian.GestionIncidentesPatrullas.patrol.repository;

import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de Patrullas.
 * Cumple con LSP (sustituible por Spring Data JPA) e ISP (expone solo consultas necesarias para la gestión de patrullas).
 */
public interface PatrolRepository extends JpaRepository<Patrol, Long> {

    // Find by status
    List<Patrol> findByStatus(PatrolStatus status);
}