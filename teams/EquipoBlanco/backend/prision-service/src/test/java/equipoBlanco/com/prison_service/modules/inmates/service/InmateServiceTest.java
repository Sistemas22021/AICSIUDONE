package equipoBlanco.com.prison_service.modules.inmates.service;

import equipoBlanco.com.prison_service.modules.inmates.dto.InmateDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.TemporaryEgressDto;
import equipoBlanco.com.prison_service.modules.inmates.dto.TemporaryReturnDto;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate.InmateStatus;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import equipoBlanco.com.prison_service.modules.postpenal.service.PostPenalService;
import equipoBlanco.com.prison_service.modules.cells.model.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InmateServiceTest {

    @Mock
    private InmateRepository inmateRepository;

    @Mock
    private PostPenalService postPenalService;

    @InjectMocks
    private InmateService inmateService;

    private Inmate inmate;
    private UUID inmateId;
    private Cell cell;

    @BeforeEach
    void setUp() {
        inmateId = UUID.randomUUID();
        cell = new Cell();
        cell.setId(UUID.randomUUID());
        cell.setIdentifier("C-101");

        inmate = Inmate.builder()
            .id(inmateId)
            .cedula("V-12345678")
            .firstName("Juan")
            .firstLastname("Perez")
            .status(InmateStatus.ACTIVO_CON_CELDA)
            .cell(cell)
            .statusHistory(new ArrayList<>())
            .build();
    }

    @Test
    void testRegisterTemporaryEgress_Success() {
        // Arrange
        TemporaryEgressDto dto = TemporaryEgressDto.builder()
            .motivoSalidaTemporal("Traslado Médico")
            .fechaSalidaTemporal(LocalDateTime.now())
            .fechaRetornoEstimada(LocalDateTime.now().plusDays(1))
            .observaciones("Cita con cardiólogo")
            .build();

        when(inmateRepository.findById(inmateId)).thenReturn(Optional.of(inmate));
        when(inmateRepository.save(any(Inmate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        InmateDto result = inmateService.registerTemporaryEgress(inmateId, dto, "OficialPruebas");

        // Assert
        assertNotNull(result);
        assertEquals(InmateStatus.ACTIVO_SALIDA_TEMPORAL, result.getStatus());
        assertEquals("Traslado Médico", result.getMotivoSalidaTemporal());
        assertEquals(1, result.getStatusHistory().size());
        assertTrue(result.getStatusHistory().get(0).contains("Salida Temporal registrada por OficialPruebas"));
        verify(inmateRepository, times(1)).save(any(Inmate.class));
    }

    @Test
    void testRegisterTemporaryEgress_Failure_AlreadyEgressed() {
        // Arrange
        inmate.setStatus(InmateStatus.ACTIVO_SALIDA_TEMPORAL);
        TemporaryEgressDto dto = TemporaryEgressDto.builder()
            .motivoSalidaTemporal("Traslado Médico")
            .fechaSalidaTemporal(LocalDateTime.now())
            .fechaRetornoEstimada(LocalDateTime.now().plusDays(1))
            .build();

        when(inmateRepository.findById(inmateId)).thenReturn(Optional.of(inmate));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            inmateService.registerTemporaryEgress(inmateId, dto, "OficialPruebas");
        });
        assertEquals("El recluso ya se encuentra en salida temporal", exception.getMessage());
        verify(inmateRepository, never()).save(any(Inmate.class));
    }

    @Test
    void testRegisterTemporaryReturn_Success() {
        // Arrange
        inmate.setStatus(InmateStatus.ACTIVO_SALIDA_TEMPORAL);
        inmate.setMotivoSalidaTemporal("Traslado Médico");
        inmate.setFechaSalidaTemporal(LocalDateTime.now().minusDays(1));
        inmate.setFechaRetornoEstimada(LocalDateTime.now().plusHours(2));

        TemporaryReturnDto dto = TemporaryReturnDto.builder()
            .fechaRetorno(LocalDateTime.now())
            .observaciones("Retorno sin novedades")
            .build();

        when(inmateRepository.findById(inmateId)).thenReturn(Optional.of(inmate));
        when(inmateRepository.save(any(Inmate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        InmateDto result = inmateService.registerTemporaryReturn(inmateId, dto, "OficialPruebas");

        // Assert
        assertNotNull(result);
        assertEquals(InmateStatus.ACTIVO_CON_CELDA, result.getStatus());
        assertNull(result.getMotivoSalidaTemporal());
        assertEquals(1, result.getStatusHistory().size());
        assertTrue(result.getStatusHistory().get(0).contains("Retorno Temporal registrado por OficialPruebas"));
        verify(inmateRepository, times(1)).save(any(Inmate.class));
    }

    @Test
    void testRegisterTemporaryReturn_Failure_NotOnTemporaryEgress() {
        // Arrange
        inmate.setStatus(InmateStatus.ACTIVO_CON_CELDA);
        TemporaryReturnDto dto = TemporaryReturnDto.builder()
            .fechaRetorno(LocalDateTime.now())
            .build();

        when(inmateRepository.findById(inmateId)).thenReturn(Optional.of(inmate));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            inmateService.registerTemporaryReturn(inmateId, dto, "OficialPruebas");
        });
        assertEquals("El recluso no se encuentra en estado de salida temporal", exception.getMessage());
        verify(inmateRepository, never()).save(any(Inmate.class));
    }
}
