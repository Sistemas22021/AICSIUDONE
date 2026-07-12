package com.azulcian.GestionIncidentesPatrullas.assignment.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.assignment.strategy.AssignmentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AssignmentStrategy assignmentStrategy;

    // Necesario porque AssignmentService recibe este repositorio por constructor (Inyección)
    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private AssignmentService assignmentService;

    private AssignmentRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new AssignmentRequestDTO();
        dto.setIncidentId(1L);
        dto.setPatrolId(2L);
    }

    // =========================================
    // TEST DELEGACIÓN CORRECTA
    // =========================================
    @Test
    void assign_shouldDelegateAssignmentToStrategy() {
        Assignment assignment = new Assignment();

        when(assignmentStrategy.execute(dto))
                .thenReturn(assignment);

        Assignment result = assignmentService.assign(dto);

        assertNotNull(result);
        verify(assignmentStrategy, times(1)).execute(dto);
    }
}
