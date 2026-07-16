package equipoBlanco.com.prison_service.modules.cells.service;

import equipoBlanco.com.prison_service.modules.cells.dto.CellDto;
import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import equipoBlanco.com.prison_service.modules.cells.model.CellAssignment;
import equipoBlanco.com.prison_service.modules.cells.repository.CellAssignmentRepository;
import equipoBlanco.com.prison_service.modules.cells.repository.CellRepository;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CellServiceTest {

    @Mock
    private CellRepository cellRepository;

    @Mock
    private InmateRepository inmateRepository;

    @Mock
    private CellAssignmentRepository cellAssignmentRepository;

    @InjectMocks
    private CellService cellService;

    @Test
    void createCell_ShouldCreateAndReturnCellDto_WhenIdentifierDoesNotExist() {
        // Arrange
        CellDto inputDto = CellDto.builder()
                .identifier("A1")
                .conductLevel(Cell.ConductLevel.ALTO)
                .lengthMeters(new BigDecimal("5.0"))
                .widthMeters(new BigDecimal("5.0"))
                .build();

        when(cellRepository.existsByIdentifier("A1")).thenReturn(false);

        Cell savedCell = Cell.builder()
                .id(UUID.randomUUID())
                .identifier("A1")
                .maxCapacity(Cell.MAX_CAPACITY)
                .conductLevel(Cell.ConductLevel.ALTO)
                .lengthMeters(new BigDecimal("5.0"))
                .widthMeters(new BigDecimal("5.0"))
                .blockedForInvestigation(false)
                .build();

        when(cellRepository.save(any(Cell.class))).thenReturn(savedCell);
        when(inmateRepository.countByCellId(savedCell.getId())).thenReturn(0);

        // Act
        CellDto result = cellService.createCell(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals("A1", result.getIdentifier());
        assertEquals("DISPONIBLE", result.getOccupancyStatus());
        verify(cellRepository, times(1)).existsByIdentifier("A1");
        verify(cellRepository, times(1)).save(any(Cell.class));
    }

    @Test
    void createCell_ShouldThrowException_WhenIdentifierAlreadyExists() {
        // Arrange
        CellDto inputDto = CellDto.builder()
                .identifier("A1")
                .build();

        when(cellRepository.existsByIdentifier("A1")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cellService.createCell(inputDto));
        assertEquals("Ya existe una celda con el identificador: A1", exception.getMessage());
        verify(cellRepository, never()).save(any(Cell.class));
    }

    @Test
    void assignInmate_ShouldAssignInmate_WhenConditionsAreMet() {
        // Arrange
        UUID cellId = UUID.randomUUID();
        UUID inmateId = UUID.randomUUID();
        String assignedBy = "admin";

        Cell cell = Cell.builder()
                .id(cellId)
                .identifier("B1")
                .maxCapacity(4)
                .blockedForInvestigation(false)
                .build();

        Inmate inmate = new Inmate();
        inmate.setId(inmateId);
        inmate.setStatus(Inmate.InmateStatus.ACTIVO_SIN_CELDA);

        when(cellRepository.findById(cellId)).thenReturn(Optional.of(cell));
        when(inmateRepository.countByCellId(cellId)).thenReturn(2); // Menos de la capacidad
        when(inmateRepository.findById(inmateId)).thenReturn(Optional.of(inmate));

        // Act
        CellDto result = cellService.assignInmate(cellId, inmateId, assignedBy);

        // Assert
        assertNotNull(result);
        assertEquals("B1", result.getIdentifier());

        ArgumentCaptor<Inmate> inmateCaptor = ArgumentCaptor.forClass(Inmate.class);
        verify(inmateRepository, times(1)).save(inmateCaptor.capture());
        Inmate savedInmate = inmateCaptor.getValue();
        assertEquals(cell, savedInmate.getCell());
        assertEquals(Inmate.InmateStatus.ACTIVO_CON_CELDA, savedInmate.getStatus());

        verify(cellAssignmentRepository, times(1)).save(any(CellAssignment.class));
    }

    @Test
    void assignInmate_ShouldThrowException_WhenCellIsBlocked() {
        // Arrange
        UUID cellId = UUID.randomUUID();
        UUID inmateId = UUID.randomUUID();

        Cell cell = Cell.builder()
                .id(cellId)
                .blockedForInvestigation(true)
                .build();

        when(cellRepository.findById(cellId)).thenReturn(Optional.of(cell));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cellService.assignInmate(cellId, inmateId, "admin"));
        assertEquals("La celda está bloqueada por investigación", exception.getMessage());
        
        verify(inmateRepository, never()).save(any(Inmate.class));
        verify(cellAssignmentRepository, never()).save(any(CellAssignment.class));
    }
}
