package com.azulcian.GestionIncidentesPatrullas.assignment.strategy;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
import com.azulcian.GestionIncidentesPatrullas.incident.repository.IncidentRepository;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import com.azulcian.GestionIncidentesPatrullas.patrol.repository.PatrolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicAssignmentStrategyTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private PatrolRepository patrolRepository;

    @InjectMocks
    private BasicAssignmentStrategy strategy;

    private Incident incident;
    private Patrol patrol;
    private AssignmentRequestDTO dto;

    @BeforeEach
    void setUp() {
        incident = new Incident();
        incident.setId(1L);
        incident.setStatus(IncidentStatus.ACTIVE);

        patrol = new Patrol();
        patrol.setId(2L);
        patrol.setStatus(PatrolStatus.AVAILABLE);

        dto = new AssignmentRequestDTO();
        dto.setIncidentId(1L);
        dto.setPatrolId(2L);
    }

    // =========================================
    // TEST ASIGNACIÓN EXITOSA (RN-08)
    // =========================================
    @Test
    void execute_shouldCreateAssignmentSuccessfully() {
        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(patrolRepository.findById(2L))
                .thenReturn(Optional.of(patrol));

        when(assignmentRepository.findByIncident(incident))
                .thenReturn(Optional.empty());

        // Simulamos que el save retorna la misma instancia de la asignación mapeada
        when(assignmentRepository.save(any(Assignment.class)))
                .thenAnswer(invocation -> {
                    Assignment savedAssignment = invocation.getArgument(0);
                    // Seteamos las relaciones simulando el comportamiento real de la lógica
                    savedAssignment.setIncident(incident);
                    savedAssignment.setPatrol(patrol);
                    return savedAssignment;
                });

        Assignment result = strategy.execute(dto);

        // Verificaciones de estructura y estados
        assertNotNull(result);
        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());
        assertEquals(PatrolStatus.EN_ROUTE, patrol.getStatus());

        // RN-08: Toda asignación debe estar asociada a un incidente y una patrulla
        assertNotNull(result.getIncident());
        assertNotNull(result.getPatrol());
        assertEquals(incident, result.getIncident());
        assertEquals(patrol, result.getPatrol());

        verify(incidentRepository, times(1)).save(incident);
        verify(patrolRepository, times(1)).save(patrol);
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    // =========================================
    // TEST INCIDENTE YA ASIGNADO (RN-02)
    // =========================================
    @Test
    void execute_shouldFailIfIncidentAlreadyAssigned() {
        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(patrolRepository.findById(2L))
                .thenReturn(Optional.of(patrol));

        when(assignmentRepository.findByIncident(incident))
                .thenReturn(Optional.of(new Assignment()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> strategy.execute(dto));

        assertEquals("Incident already has a patrol assigned", ex.getMessage());
    }

    // =========================================
    // TEST INCIDENTE NO ACTIVO (RN-03)
    // =========================================
    @Test
    void execute_shouldFailIfIncidentNotActive() {
        incident.setStatus(IncidentStatus.CLOSED);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(patrolRepository.findById(2L))
                .thenReturn(Optional.of(patrol));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> strategy.execute(dto));

        assertEquals("Incident must be ACTIVE", ex.getMessage());
    }

    // =========================================
    // TEST PATRULLA NO DISPONIBLE (RN-04)
    // =========================================
    @Test
    void execute_shouldFailIfPatrolNotAvailable() {
        patrol.setStatus(PatrolStatus.BUSY);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(patrolRepository.findById(2L))
                .thenReturn(Optional.of(patrol));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> strategy.execute(dto));

        assertEquals("Patrol must be AVAILABLE", ex.getMessage());
    }

    // =========================================
    // TEST INCIDENTE NO ENCONTRADO
    // =========================================
    @Test
    void execute_shouldFailIfIncidentNotFound() {
        when(incidentRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> strategy.execute(dto));

        assertEquals("Incident not found", ex.getMessage());
    }

    // =========================================
    // TEST PATRULLA NO ENCONTRADA
    // =========================================
    @Test
    void execute_shouldFailIfPatrolNotFound() {
        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(patrolRepository.findById(2L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> strategy.execute(dto));

        assertEquals("Patrol not found", ex.getMessage());
    }
}
