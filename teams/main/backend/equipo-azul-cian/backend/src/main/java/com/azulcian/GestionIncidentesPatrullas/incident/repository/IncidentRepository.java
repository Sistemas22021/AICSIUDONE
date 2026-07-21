package com.azulcian.GestionIncidentesPatrullas.incident.repository;

import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de Incidentes.
 * Cumple con LSP (sustituible por Spring Data JPA) e ISP (expone solo consultas necesarias para el dominio).
 */
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // Recent created incidents
    List<Incident> findTop10ByOrderByCreatedAtDesc();

    // Recent updated incidents
    List<Incident> findTop10ByOrderByUpdatedAtDesc();
}