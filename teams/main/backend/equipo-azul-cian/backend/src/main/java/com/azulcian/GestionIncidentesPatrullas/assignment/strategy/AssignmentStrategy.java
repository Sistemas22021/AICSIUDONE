package com.azulcian.GestionIncidentesPatrullas.assignment.strategy;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;

// =============================================================
// PRINCIPIO SOLID: O - Open/Closed Principle (OCP)
// -------------------------------------------------------------
// Define el contrato de las estrategias de asignación.
//
// Permite agregar nuevas estrategias sin modificar el código
// existente, facilitando la extensión del sistema.
//
// ✔ Abierto a extensión, cerrado a modificación.
// =============================================================
// PRINCIPIO SOLID: L - Liskov Substitution Principle (LSP)
// -------------------------------------------------------------
// Cualquier implementación de AssignmentStrategy puede sustituir
// a otra implementación sin afectar el funcionamiento del sistema.
//
// Todas las estrategias respetan el contrato definido por execute(),
// permitiendo su intercambio mediante la abstracción.
//
// ✔ Garantiza la sustituibilidad de estrategias.
// =============================================================
public interface AssignmentStrategy {

    Assignment execute(AssignmentRequestDTO dto);
}