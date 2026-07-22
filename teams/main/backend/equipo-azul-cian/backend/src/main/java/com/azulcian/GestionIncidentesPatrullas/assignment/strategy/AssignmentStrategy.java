package com.azulcian.GestionIncidentesPatrullas.assignment.strategy;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;

/**
 * Estrategia de Asignación.
 * Cumple con OCP (permite añadir nuevas estrategias sin modificar código) y LSP (garantiza la sustituibilidad de cualquier implementación).
 */
public interface AssignmentStrategy {

    Assignment execute(AssignmentRequestDTO dto);
}