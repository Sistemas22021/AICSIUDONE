package com.azulcian.GestionIncidentesPatrullas.assignment.strategy;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;

// =============================================================
// PRINCIPIO SOLID: O - Open/Closed Principle (OCP)
// -------------------------------------------------------------
// Esta interfaz define el contrato de las estrategias de asignación.
//
// El sistema está abierto a extensión porque pueden crearse nuevas
// estrategias (por ejemplo: asignación por proximidad o prioridad)
// sin modificar el código existente.
//
// Está cerrado a modificación porque este contrato no cambia,
// garantizando estabilidad del sistema.
//
// ✔ Permite extender el comportamiento sin modificar el núcleo.
// =============================================================
public interface AssignmentStrategy {

    Assignment execute(AssignmentRequestDTO dto);
}