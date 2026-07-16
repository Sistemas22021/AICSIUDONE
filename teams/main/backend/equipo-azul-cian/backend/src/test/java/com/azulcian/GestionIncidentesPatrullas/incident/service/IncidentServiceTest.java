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

    @Test
    void getIncidentById_shouldThrowExceptionWhenNotFound() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> incidentService.getIncidentById(1L)
        );

        assertEquals(
                "Incident not found with id: 1",
                exception.getMessage()
        );
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

        // Se cambia a IN_PROGRESS porque CLOSED ahora lanza excepción en este metodo
        Incident result = incidentService.updateStatus(1L, IncidentStatus.IN_PROGRESS);

        assertEquals(IncidentStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void updateStatus_shouldRejectClosedStatus() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> incidentService.updateStatus(
                        1L,
                        IncidentStatus.CLOSED
                )
        );

        assertEquals(
                "Use close endpoint to close incidents",
                exception.getMessage()
        );
    }

    // =========================================
    // TEST GET RECENT
    // =========================================
    @Test
    void getRecentCreated_shouldReturnIncidents() {

        when(incidentRepository.findTop10ByOrderByCreatedAtDesc())
                .thenReturn(List.of(incident));

        List<Incident> result =
                incidentService.getRecentCreated();

        assertEquals(1, result.size());
    }

    @Test
    void getRecentUpdates_shouldReturnIncidents() {

        when(incidentRepository.findTop10ByOrderByUpdatedAtDesc())
                .thenReturn(List.of(incident));

        List<Incident> result =
                incidentService.getRecentUpdates();

        assertEquals(1, result.size());
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
    // TEST GET DETAILS & ALL INCIDENTS
    // =========================================
    @Test
    void getIncidentDetailById_shouldReturnDTO() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(assignmentRepository.findByIncident(incident))
                .thenReturn(Optional.empty());

        var result =
                incidentService.getIncidentDetailById(1L);

        assertEquals(incident.getId(), result.getId());
        assertEquals(incident.getType(), result.getType());
    }

    @Test
    void getAllIncidents_shouldReturnDTOList() {

        when(incidentRepository.findTop10ByOrderByCreatedAtDesc())
                .thenReturn(List.of(incident));

        when(assignmentRepository.findByIncident(incident))
                .thenReturn(Optional.empty());

        var result = incidentService.getAllIncidents();

        assertEquals(1, result.size());
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
