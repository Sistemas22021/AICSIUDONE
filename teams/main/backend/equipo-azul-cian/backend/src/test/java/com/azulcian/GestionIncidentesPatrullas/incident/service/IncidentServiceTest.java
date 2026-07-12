package com.azulcian.GestionIncidentesPatrullas.incident.service;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private PatrolRepository patrolRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Incident incident;

    @BeforeEach
    void setUp() {
        incident = new Incident();
        incident.setId(1L);
        incident.setType("ROBO");
        incident.setDescription("Test");
        incident.setLatitude(4.61);
        incident.setLongitude(-74.08);
        incident.setStatus(IncidentStatus.ACTIVE);
    }

    // =========================================
    // TEST CREATE INCIDENT
    // =========================================
    @Test
    void createIncident_shouldForceActiveStatus() {

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident result = incidentService.createIncident(incident);

        assertEquals(IncidentStatus.ACTIVE, result.getStatus());
        verify(incidentRepository, times(1)).save(incident);
    }

    // =========================================
    // TEST GET BY ID
    // =========================================
    @Test
    void getIncidentById_shouldReturnIncident() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        Incident result = incidentService.getIncidentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // =========================================
    // TEST UPDATE STATUS
    // =========================================
    @Test
    void updateStatus_shouldChangeStatus() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Se cambia a IN_PROGRESS porque CLOSED ahora lanza excepción en este método
        Incident result = incidentService.updateStatus(1L, IncidentStatus.IN_PROGRESS);

        assertEquals(IncidentStatus.IN_PROGRESS, result.getStatus());
    }

    // =========================================
    // TEST GET SUMMARY
    // =========================================
    @Test
    void getSummary_shouldCountStatusesCorrectly() {

        Incident i1 = new Incident();
        i1.setStatus(IncidentStatus.ACTIVE);

        Incident i2 = new Incident();
        i2.setStatus(IncidentStatus.IN_PROGRESS);

        Incident i3 = new Incident();
        i3.setStatus(IncidentStatus.CLOSED);

        when(incidentRepository.findAll())
                .thenReturn(List.of(i1, i2, i3));

        var result = incidentService.getSummary();

        assertEquals(1, result.getActive());
        assertEquals(1, result.getInProgress());
        assertEquals(1, result.getClosed());
        assertEquals(3, result.getTotal());
    }

    // =========================================
    // TEST CLOSE INCIDENT (CORE BUSINESS LOGIC)
    // =========================================
    @Test
    void closeIncident_shouldCloseIncidentAndFreePatrol() {

        incident.setStatus(IncidentStatus.IN_PROGRESS);

        Patrol patrol = new Patrol();
        patrol.setStatus(PatrolStatus.BUSY);

        Assignment assignment = new Assignment();
        assignment.setPatrol(patrol);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(assignmentRepository.findByIncident(incident))
                .thenReturn(Optional.of(assignment));

        when(incidentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(patrolRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(assignmentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident result = incidentService.closeIncident(1L);

        assertEquals(IncidentStatus.CLOSED, result.getStatus());
        assertEquals(PatrolStatus.AVAILABLE, patrol.getStatus());
        assertNotNull(assignment.getFinishedAt());
    }

    @Test
    void closeIncident_shouldFailWhenPatrolIsNotBusy() {

        incident.setStatus(IncidentStatus.IN_PROGRESS);

        Patrol patrol = new Patrol();
        patrol.setStatus(PatrolStatus.EN_ROUTE);

        Assignment assignment = new Assignment();
        assignment.setPatrol(patrol);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(assignmentRepository.findByIncident(incident))
                .thenReturn(Optional.of(assignment));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> incidentService.closeIncident(1L)
        );

        assertEquals(
                "Incident can only be closed when patrol is BUSY",
                exception.getMessage()
        );
    }
}
