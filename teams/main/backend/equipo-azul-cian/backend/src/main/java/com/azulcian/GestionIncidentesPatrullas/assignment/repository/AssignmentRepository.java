package com.azulcian.GestionIncidentesPatrullas.assignment.repository;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// =============================================================
// PRINCIPIO SOLID: I - Interface Segregation Principle (ISP)
// -------------------------------------------------------------
// Define métodos específicos para la gestión de asignaciones,
// evitando depender de operaciones innecesarias.
//
// ✔ Mantiene una interfaz pequeña y enfocada al dominio.
// =============================================================
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByIncident(Incident incident);

    List<Assignment> findByFinishedAtIsNull();

    Optional<Assignment> findByPatrolAndFinishedAtIsNull(Patrol patrol);
}