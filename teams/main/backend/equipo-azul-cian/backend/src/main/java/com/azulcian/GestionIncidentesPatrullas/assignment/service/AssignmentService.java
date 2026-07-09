package com.azulcian.GestionIncidentesPatrullas.assignment.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.assignment.strategy.AssignmentStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

// =============================================================
// PRINCIPIO SOLID: S - Single Responsibility Principle (SRP)
// -------------------------------------------------------------
// Esta clase tiene una única responsabilidad: coordinar el Caso de
// Uso CU-03 "Asignación Operativa".
//
// Su función es actuar como punto de entrada del caso de uso,
// delegando la lógica de negocio a la estrategia de asignación.
//
// Responsabilidades que NO tiene:
// • No contiene reglas de negocio complejas.
// • No decide cómo se asigna una patrulla.
// • No gestiona validaciones internas.
//
// ✔ Mantiene una única razón de cambio: la coordinación del caso de uso.
// =============================================================
//
// =============================================================
// PRINCIPIO SOLID: D - Dependency Inversion Principle (DIP)
// -------------------------------------------------------------
// Esta clase depende de abstracciones (AssignmentStrategy y
// AssignmentRepository), no de implementaciones concretas.
//
// Spring inyecta automáticamente las dependencias, lo que:
// • Reduce el acoplamiento.
// • Permite testabilidad.
// • Facilita extensibilidad.
//
// ✔ Cumple DIP correctamente.
// =============================================================
//
// =============================================================
// PRINCIPIO SOLID: O - Open/Closed Principle (OCP)
// -------------------------------------------------------------
// El comportamiento de asignación está encapsulado en AssignmentStrategy.
//
// Esto permite extender el sistema agregando nuevas estrategias como:
// • ProximityAssignmentStrategy
// • PriorityAssignmentStrategy
// • LoadBalancedAssignmentStrategy
//
// sin modificar este servicio.
//
// ✔ Abierto a extensión, cerrado a modificación.
// =============================================================
public class AssignmentService {

    private final AssignmentStrategy assignmentStrategy;
    private final AssignmentRepository assignmentRepository;

    public AssignmentService(
            AssignmentStrategy assignmentStrategy,
            AssignmentRepository assignmentRepository
    ) {
        this.assignmentStrategy = assignmentStrategy;
        this.assignmentRepository = assignmentRepository;
    }

    // ==========================================================
    // CU-03: ASIGNACIÓN OPERATIVA
    // ----------------------------------------------------------
    // Punto de entrada del caso de uso.
    // Toda la lógica de asignación se delega a la estrategia.
    // ==========================================================
    public Assignment assign(AssignmentRequestDTO dto) {
        return assignmentStrategy.execute(dto);
    }

    // ==========================================================
    // CONSULTA DE ASIGNACIONES ACTIVAS
    // ==========================================================
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findByFinishedAtIsNull();
    }
}