package equipoBlanco.com.prison_service.modules.postpenal.service;

import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import equipoBlanco.com.prison_service.modules.inmates.model.Inmate;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import equipoBlanco.com.prison_service.modules.postpenal.dto.AssignOfficerDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpProfileDto;
import equipoBlanco.com.prison_service.modules.postpenal.dto.ExpedienteDto;
import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import equipoBlanco.com.prison_service.modules.postpenal.repository.ExpedienteSeguimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostPenalServiceTest {

    @Mock
    private ExpedienteSeguimientoRepository expedienteSeguimientoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private InmateRepository inmateRepository;

    @InjectMocks
    private PostPenalService postPenalService;

    @Test
    void createBaseProfile_ShouldSaveExpedienteAndAlerta() {
        // Arrange
        Inmate inmate = Inmate.builder()
                .id(UUID.randomUUID())
                .cedula("V-123456")
                .build();
        LocalDate fechaEgreso = LocalDate.now();

        // Act
        postPenalService.createBaseProfile(inmate, fechaEgreso);

        // Assert
        verify(expedienteSeguimientoRepository, times(1)).save(any(ExpedienteSeguimiento.class));
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void assignOfficer_ShouldAssignAndCreateAlerta() {
        // Arrange
        UUID expedienteId = UUID.randomUUID();
        AssignOfficerDto dto = new AssignOfficerDto();
        dto.setOficialNombre("Oficial 1");
        dto.setOficialCedula("V-111");

        ExpedienteSeguimiento expediente = ExpedienteSeguimiento.builder()
                .id(expedienteId)
                .idRecluso(UUID.randomUUID())
                .historialAsignaciones(new ArrayList<>())
                .build();

        Inmate inmate = Inmate.builder().cedula("V-123").firstName("Test").firstLastname("Test").build();

        when(expedienteSeguimientoRepository.findById(expedienteId)).thenReturn(Optional.of(expediente));
        when(inmateRepository.findById(expediente.getIdRecluso())).thenReturn(Optional.of(inmate));

        // Act
        ExpedienteDto result = postPenalService.assignOfficer(expedienteId, dto);

        // Assert
        assertNotNull(result);
        assertEquals("asignado", result.getEstado());
        assertEquals("Oficial 1", result.getOficialAsignadoNombre());
        verify(expedienteSeguimientoRepository, times(1)).save(expediente);
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void completeProfile_ShouldUpdateAndChangeState() {
        // Arrange
        UUID expedienteId = UUID.randomUUID();
        ExpProfileDto dto = new ExpProfileDto();
        dto.setDomicilio("Calle 1");
        dto.setMunicipio("Municipio 1");
        dto.setContactoEmergenciaNombre("Contacto");
        dto.setContactoEmergenciaTelefono("04141234567");
        dto.setNivelRiesgo("Bajo");

        ExpedienteSeguimiento expediente = ExpedienteSeguimiento.builder()
                .id(expedienteId)
                .estado("Perfil Incompleto")
                .build();

        when(expedienteSeguimientoRepository.findById(expedienteId)).thenReturn(Optional.of(expediente));

        // Act
        ExpedienteDto result = postPenalService.completeProfile(expedienteId, dto);

        // Assert
        assertNotNull(result);
        assertEquals("completado", result.getEstado()); // Assuming completarPerfil() sets state to "completado"
        assertEquals("Calle 1", result.getDomicilio());
        verify(expedienteSeguimientoRepository, times(1)).save(expediente);
    }
}
