package com.azulcian.GestionIncidentesPatrullas.patrol.service;

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
    // UPDATE STATUS
    // =========================================
    @Test
    void updateStatus_shouldChangeStatus() {

        when(patrolRepository.findById(1L))
                .thenReturn(Optional.of(patrol));

        when(patrolRepository.save(any(Patrol.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patrol result = patrolService.updateStatus(1L, PatrolStatus.BUSY);

        assertEquals(PatrolStatus.BUSY, result.getStatus());
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
    // TEST ERROR: PATROL NOT FOUND
    // =========================================
    @Test
    void getPatrolById_shouldThrowException_whenNotFound() {

        when(patrolRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patrolService.getPatrolById(99L));

        assertEquals("Patrol not found with id: 99", ex.getMessage());
    }
}
