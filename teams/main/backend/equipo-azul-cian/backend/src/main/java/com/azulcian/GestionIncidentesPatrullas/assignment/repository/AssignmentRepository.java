package com.azulcian.GestionIncidentesPatrullas.assignment.repository;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Asignaciones.
 * Cumple con ISP al exponer métodos específicos para el dominio de asignaciones sin sobrecargar la interfaz.
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByIncident(Incident incident);

    List<Assignment> findByFinishedAtIsNull();

    Optional<Assignment> findByPatrolAndFinishedAtIsNull(Patrol patrol);
}