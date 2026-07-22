package com.azulcian.GestionIncidentesPatrullas.patrol.service;

import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.repository.AssignmentRepository;
import com.azulcian.GestionIncidentesPatrullas.incident.model.Incident;
import com.azulcian.GestionIncidentesPatrullas.incident.model.IncidentStatus;
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
class PatrolServiceTest {

    @Mock
    private PatrolRepository patrolRepository;

    @Mock
    private AssignmentRepository assignmentRepository; // Nuevo mock inyectado

    @InjectMocks
    private PatrolService patrolService;

    private Patrol patrol;

    @BeforeEach
    void setUp() {
        patrol = new Patrol();
        patrol.setId(1L);
        patrol.setCode("P-001");
        patrol.setOfficerName("Juan Perez");
        patrol.setStatus(PatrolStatus.AVAILABLE);
    }

    // =========================================
    // CREATE PATROL
    // =========================================
    @Test
    void createPatrol_shouldSetDefaultStatusIfNull() {

        patrol.setStatus(null);

        when(patrolRepository.save(any(Patrol.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patrol result = patrolService.createPatrol(patrol);

        assertEquals(PatrolStatus.AVAILABLE, result.getStatus());
        verify(patrolRepository, times(1)).save(patrol);
    }

    @Test
    void createPatrol_shouldAllowOutOfServiceAsInitialStatus() {

        patrol.setStatus(PatrolStatus.OUT_OF_SERVICE);

        when(patrolRepository.save(any(Patrol.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patrol result = patrolService.createPatrol(patrol);

        assertEquals(PatrolStatus.OUT_OF_SERVICE, result.getStatus());
        verify(patrolRepository, times(1)).save(patrol);
    }

    @Test
    void createPatrol_shouldRejectOperationalStatuses() {

        PatrolStatus[] invalidStatuses = {
                PatrolStatus.EN_ROUTE,
                PatrolStatus.BUSY
        };

        for (PatrolStatus status : invalidStatuses) {

            patrol.setStatus(status);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> patrolService.createPatrol(patrol)
            );

            assertEquals(
                    "Una patrulla solo puede registrarse como AVAILABLE o OUT_OF_SERVICE.",
                    ex.getMessage()
            );

            verify(patrolRepository, never()).save(any(Patrol.class));

            reset(patrolRepository);
        }
    }

    // =========================================
    // GET ALL PATROLS
    // =========================================
    @Test
    void getAllPatrols_shouldReturnList() {

        when(patrolRepository.findAll())
                .thenReturn(List.of(patrol));

        List<Patrol> result = patrolService.getAllPatrols();

        assertEquals(1, result.size());
        assertEquals("P-001", result.get(0).getCode());
    }

    // =========================================
    // GET BY ID
    // =========================================
    @Test
    void getPatrolById_shouldReturnPatrol() {

        when(patrolRepository.findById(1L))
                .thenReturn(Optional.of(patrol));

        Patrol result = patrolService.getPatrolById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // =========================================
    // UPDATE STATUS (GESTIÓN ADMINISTRATIVA)
    // =========================================
    @Test
    void updateStatus_shouldChangeToOutOfService() {

        when(patrolRepository.findById(1L))
                .thenReturn(Optional.of(patrol));

        when(patrolRepository.save(any(Patrol.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patrol result = patrolService.updateStatus(1L, PatrolStatus.OUT_OF_SERVICE);

        assertEquals(PatrolStatus.OUT_OF_SERVICE, result.getStatus());
    }

    @Test
    void updateStatus_shouldNotAllowManualChangeFromBusy() {

        patrol.setStatus(PatrolStatus.BUSY);

        when(patrolRepository.findById(1L))
                .thenReturn(Optional.of(patrol));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> patrolService.updateStatus(1L, PatrolStatus.AVAILABLE)
        );

        assertEquals(
                "Las patrullas EN_ROUTE o BUSY no pueden cambiar su estado manualmente.",
                ex.getMessage()
        );
    }

    // =========================================
    // GET AVAILABLE PATROLS
    // =========================================
    @Test
    void getAvailablePatrols_shouldReturnOnlyAvailable() {

        Patrol p2 = new Patrol();
        p2.setStatus(PatrolStatus.BUSY);

        when(patrolRepository.findByStatus(PatrolStatus.AVAILABLE))
                .thenReturn(List.of(patrol));

        List<Patrol> result = patrolService.getAvailablePatrols();

        assertEquals(1, result.size());
        assertEquals(PatrolStatus.AVAILABLE, result.get(0).getStatus());
    }

    // =========================================
    // MARK PATROL AS ARRIVED
    // =========================================
    @Test
    void markAsArrived_shouldChangePatrolToBusy() {

        patrol.setStatus(PatrolStatus.EN_ROUTE);

        Incident incident = new Incident();
        incident.setLatitude(10.0);
        incident.setLongitude(-60.0);
        incident.setStatus(IncidentStatus.IN_PROGRESS);

        Assignment assignment = new Assignment();
        assignment.setPatrol(patrol);
        assignment.setIncident(incident);

        when(patrolRepository.findById(1L))
                .thenReturn(Optional.of(patrol));

        when(assignmentRepository.findByPatrolAndFinishedAtIsNull(patrol))
                .thenReturn(Optional.of(assignment));

        when(patrolRepository.save(any(Patrol.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patrol result = patrolService.markAsArrived(1L);

        assertEquals(PatrolStatus.BUSY, result.getStatus());
        assertEquals(10.0, result.getLatitude());
        assertEquals(-60.0, result.getLongitude());
    }

    // =========================================
    // TEST ERROR: PATROL NOT FOUND
    // =========================================
    @Test
    void getPatrolById_shouldThrowException_whenNotFound() {

        when(patrolRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patrolService.getPatrolById(99L));

        // Corregido para coincidir con el mensaje real en español del servicio
        assertEquals("No se encontró la patrulla con el ID: 99", ex.getMessage());
    }
}
