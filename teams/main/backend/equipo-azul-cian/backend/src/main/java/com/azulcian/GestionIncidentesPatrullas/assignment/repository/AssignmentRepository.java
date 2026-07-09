package com.azulcian.GestionIncidentesPatrullas.assignment.repository;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// =============================================================
// PRINCIPIO SOLID: L - Liskov Substitution Principle (LSP)
// -------------------------------------------------------------
// AssignmentRepository hereda de JpaRepository, lo que permite
// que pueda ser sustituido por su tipo base sin afectar el
// comportamiento del sistema.
//
// Durante el Caso de Uso CU-03 "Asignación Operativa", el servicio
// utiliza este repositorio como una abstracción del acceso a datos,
// sin depender de su implementación concreta.
//
// Spring Data JPA garantiza la implementación automática,
// respetando el contrato definido por JpaRepository.
//
// ✔ Cumple el Principio de Sustitución de Liskov (LSP).
// =============================================================
//
// =============================================================
// PRINCIPIO SOLID: I - Interface Segregation Principle (ISP)
// -------------------------------------------------------------
// Esta interfaz está diseñada específicamente para el dominio de
// asignaciones dentro del Caso de Uso CU-03.
//
// No obliga a los clientes a depender de métodos que no utilizan,
// manteniendo una interfaz pequeña, cohesiva y enfocada.
//
// Métodos expuestos:
//
// • findByIncident(): permite validar si un incidente ya tiene asignación.
// • findByFinishedAtIsNull(): permite obtener asignaciones activas.
// • findByPatrolAndFinishedAtIsNull(): permite obtener la asignación activa de una patrulla.
//
// Esto evita una interfaz sobrecargada con responsabilidades ajenas
// al dominio de asignación.
//
// ✔ Cumple el principio de Segregación de Interfaces (ISP).
// =============================================================
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByIncident(Incident incident);

    List<Assignment> findByFinishedAtIsNull();

    Optional<Assignment> findByPatrolAndFinishedAtIsNull(Patrol patrol);
}