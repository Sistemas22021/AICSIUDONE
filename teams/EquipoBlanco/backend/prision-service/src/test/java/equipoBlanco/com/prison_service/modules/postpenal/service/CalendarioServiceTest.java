package equipoBlanco.com.prison_service.modules.postpenal.service;

import equipoBlanco.com.prison_service.modules.postpenal.model.CalendarioPresentacion;
import equipoBlanco.com.prison_service.modules.postpenal.model.ExpedienteSeguimiento;
import equipoBlanco.com.prison_service.modules.postpenal.repository.CalendarioPresentacionRepository;
import equipoBlanco.com.prison_service.modules.postpenal.repository.ExpedienteSeguimientoRepository;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import equipoBlanco.com.prison_service.modules.inmates.repository.InmateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalendarioServiceTest {

    @Mock
    private CalendarioPresentacionRepository calendarioRepository;

    @Mock
    private ExpedienteSeguimientoRepository expedienteSeguimientoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private InmateRepository inmateRepository;

    @InjectMocks
    private CalendarioService calendarioService;

    private UUID expedienteId;
    private CalendarioPresentacion presentacionVencida;
    private ExpedienteSeguimiento expediente;

    @BeforeEach
    void setUp() {
        expedienteId = UUID.randomUUID();

        presentacionVencida = CalendarioPresentacion.builder()
            .id(UUID.randomUUID())
            .expedienteId(expedienteId)
            .fechaProgramada(LocalDate.now().minusDays(1))
            .estado("PENDIENTE")
            .observaciones("")
            .build();

        expediente = ExpedienteSeguimiento.builder()
            .id(expedienteId)
            .idRecluso(UUID.randomUUID())
            .estado("asignado")
            .contadorIncumplimientos(0)
            .oficialAsignadoNombre("Oficial Pruebas")
            .oficialAsignadoCedula("V-12345")
            .build();
    }

    @Test
    void testProcesarPresentacionesVencidas_ExitoPrimerIncumplimiento() {
        // Arrange
        when(calendarioRepository.findByFechaProgramadaLessThanEqualAndEstado(any(LocalDate.class), eq("PENDIENTE")))
            .thenReturn(List.of(presentacionVencida));
        when(expedienteSeguimientoRepository.findById(expedienteId)).thenReturn(Optional.of(expediente));

        // Act
        calendarioService.procesarPresentacionesVencidas();

        // Assert
        assertEquals("INCUMPLIDA", presentacionVencida.getEstado());
        assertTrue(presentacionVencida.getObservaciones().contains("Detectado por sistema"));
        assertEquals(1, expediente.getContadorIncumplimientos());
        assertNotEquals("alerta_critica", expediente.getEstado());

        verify(calendarioRepository, times(1)).save(presentacionVencida);
        verify(expedienteSeguimientoRepository, times(1)).save(expediente);

        ArgumentCaptor<Alerta> alertaCaptor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository, times(1)).save(alertaCaptor.capture());
        Alerta savedAlerta = alertaCaptor.getValue();
        assertEquals(1, savedAlerta.getNivel());
        assertEquals("Oficial Pruebas", savedAlerta.getDestinatario());
        assertEquals("activa", savedAlerta.getEstado());
        assertTrue(savedAlerta.getAccionRequerida().contains("Incumplimiento #1"));
    }

    @Test
    void testProcesarPresentacionesVencidas_EscalacionAlertaCritica() {
        // Arrange
        expediente.setContadorIncumplimientos(2); // El siguiente será el 3ro

        when(calendarioRepository.findByFechaProgramadaLessThanEqualAndEstado(any(LocalDate.class), eq("PENDIENTE")))
            .thenReturn(List.of(presentacionVencida));
        when(expedienteSeguimientoRepository.findById(expedienteId)).thenReturn(Optional.of(expediente));

        // Act
        calendarioService.procesarPresentacionesVencidas();

        // Assert
        assertEquals("INCUMPLIDA", presentacionVencida.getEstado());
        assertEquals(3, expediente.getContadorIncumplimientos());
        assertEquals("alerta_critica", expediente.getEstado());

        verify(calendarioRepository, times(1)).save(presentacionVencida);
        verify(expedienteSeguimientoRepository, times(1)).save(expediente);

        ArgumentCaptor<Alerta> alertaCaptor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository, times(1)).save(alertaCaptor.capture());
        Alerta savedAlerta = alertaCaptor.getValue();
        assertEquals(3, savedAlerta.getNivel());
        assertEquals("Supervisor", savedAlerta.getDestinatario());
        assertEquals("activa", savedAlerta.getEstado());
        assertTrue(savedAlerta.getAccionRequerida().contains("CRÍTICO: Incumplimiento #3"));
    }

    @Test
    void testProcesarPresentacionesVencidas_SinPresentacionesVencidas() {
        // Arrange
        when(calendarioRepository.findByFechaProgramadaLessThanEqualAndEstado(any(LocalDate.class), eq("PENDIENTE")))
            .thenReturn(Collections.emptyList());

        // Act
        calendarioService.procesarPresentacionesVencidas();

        // Assert
        verify(calendarioRepository, never()).save(any(CalendarioPresentacion.class));
        verify(expedienteSeguimientoRepository, never()).save(any(ExpedienteSeguimiento.class));
        verify(alertaRepository, never()).save(any(Alerta.class));
    }
}
