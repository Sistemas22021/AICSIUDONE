package com.azulcian.GestionIncidentesPatrullas.patrol.repository;

import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// =============================================================
// PRINCIPIO SOLID: L - Liskov Substitution Principle (LSP)
// -------------------------------------------------------------
// PatrolRepository hereda el comportamiento de JpaRepository,
// permitiendo que Spring lo sustituya por su implementación
// automática sin afectar la lógica del sistema.
//
// En el Caso de Uso CU-03 "Asignación Operativa", el
// AssignmentService utiliza este repositorio sin depender de
// detalles de implementación interna.
//
// ✔ Cumple el Principio de Sustitución de Liskov (LSP).
// =============================================================
//
// =============================================================
// PRINCIPIO SOLID: I - Interface Segregation Principle (ISP)
// -------------------------------------------------------------
// Este repositorio expone únicamente los métodos necesarios para
// la gestión de patrullas dentro del sistema.
//
// Evita la exposición de métodos innecesarios, manteniendo una
// interfaz pequeña y especializada.
//
// Metodo expuesto:
// • findByStatus(): permite consultar patrullas según su estado.
//
// Esto asegura que los consumidores del repositorio no dependan
// de funcionalidades que no utilizan.
//
// ✔ Cumple el principio de Segregación de Interfaces (ISP).
// =============================================================
public interface PatrolRepository extends JpaRepository<Patrol, Long> {

    // =========================================
    // FIND BY STATUS
    // =========================================
    List<Patrol> findByStatus(PatrolStatus status);
}