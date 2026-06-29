package com.azulcian.GestionIncidentesPatrullas.assignment.repository;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByIncident(Incident incident);

    List<Assignment> findByFinishedAtIsNull();
}