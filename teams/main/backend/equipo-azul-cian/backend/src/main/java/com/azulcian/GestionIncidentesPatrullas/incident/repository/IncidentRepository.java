package com.azulcian.GestionIncidentesPatrullas.incident.repository;

import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// =============================================================
// PRINCIPIO SOLID: L - Liskov Substitution Principle (LSP)
// -------------------------------------------------------------
// Este repositorio extiende JpaRepository, por lo que puede ser
// sustituido por cualquier implementación compatible sin afectar
// el comportamiento del sistema.
//
// Durante el Caso de Uso CU-03 "Asignación Operativa", el
// AssignmentService utiliza IncidentRepository sin depender de
// detalles de implementación interna.
//
// Spring Data JPA proporciona la implementación automáticamente,
// respetando el contrato definido por JpaRepository.
//
// ✔ Cumple el Principio de Sustitución de Liskov (LSP).
// =============================================================
//
// =============================================================
// PRINCIPIO SOLID: I - Interface Segregation Principle (ISP)
// -------------------------------------------------------------
// Este repositorio expone únicamente operaciones necesarias para
// el dominio de incidentes dentro del sistema.
//
// No se incluyen métodos genéricos o innecesarios, evitando que
// los servicios dependan de funcionalidades que no utilizan.
//
// Métodos expuestos:
// • findTop10ByOrderByCreatedAtDesc(): consulta de incidentes recientes.
// • findTop10ByOrderByUpdatedAtDesc(): consulta de incidentes actualizados.
//
// Esto mantiene una interfaz cohesionada y enfocada exclusivamente
// en las necesidades del sistema.
//
// ✔ Cumple el principio de Segregación de Interfaces (ISP).
// =============================================================
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