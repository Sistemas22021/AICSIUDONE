package com.azulcian.GestionIncidentesPatrullas.incident.repository;

import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // =========================================
    // RECENT CREATED INCIDENTS
    // =========================================
    List<Incident> findTop10ByOrderByCreatedAtDesc();

    // =========================================
    // RECENT UPDATED INCIDENTS
    // =========================================
    List<Incident> findTop10ByOrderByUpdatedAtDesc();
}