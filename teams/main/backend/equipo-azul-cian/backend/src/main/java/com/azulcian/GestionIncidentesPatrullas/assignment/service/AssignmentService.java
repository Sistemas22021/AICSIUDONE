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
// Coordina el Caso de Uso CU-03 "Asignación Operativa",
// delegando la lógica de negocio a la estrategia correspondiente.
//
// ✔ Mantiene una única responsabilidad.
// =============================================================
// PRINCIPIO SOLID: D - Dependency Inversion Principle (DIP)
// -------------------------------------------------------------
// Depende de abstracciones como AssignmentStrategy,
// reduciendo el acoplamiento con implementaciones concretas.
//
// ✔ Facilita mantenimiento y pruebas.
// =============================================================
// PRINCIPIO SOLID: O - Open/Closed Principle (OCP)
// -------------------------------------------------------------
// Permite cambiar o agregar estrategias de asignación sin
// modificar la lógica del servicio.
//
// ✔ Facilita la extensión del sistema.
// =============================================================
// PRINCIPIO SOLID: L - Liskov Substitution Principle (LSP)
// -------------------------------------------------------------
// Al trabajar con AssignmentStrategy, cualquier implementación
// compatible puede sustituir a otra sin afectar el servicio.
//
// ✔ Permite intercambiar estrategias manteniendo el contrato.
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
    // Punto de entrada del caso de uso. Toda la lógica de asignación se delega a la estrategia.
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