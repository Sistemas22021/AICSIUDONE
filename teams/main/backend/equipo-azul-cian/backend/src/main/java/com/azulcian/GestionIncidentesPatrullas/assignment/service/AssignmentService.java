package com.azulcian.GestionIncidentesPatrullas.assignment.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.assignment.strategy.AssignmentStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión de asignaciones (CU-03: Asignación Operativa).
 * Cumple con SRP (coordinación del caso de uso), DIP (depende de la abstracción AssignmentStrategy),
 * OCP y LSP (permite extender e intercambiar estrategias sin alterar el servicio).
 */
@Service
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

    // Punto de entrada del CU-03: delega la lógica de asignación a la estrategia.
    public Assignment assign(AssignmentRequestDTO dto) {
        return assignmentStrategy.execute(dto);
    }

    // Consulta de asignaciones activas
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findByFinishedAtIsNull();
    }
}