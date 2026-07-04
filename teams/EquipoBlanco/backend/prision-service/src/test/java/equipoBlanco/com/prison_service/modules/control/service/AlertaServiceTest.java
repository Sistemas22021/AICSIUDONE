package equipoBlanco.com.prison_service.modules.control.service;

import equipoBlanco.com.prison_service.modules.control.dto.AlertaDto;
import equipoBlanco.com.prison_service.modules.control.dto.AtenderAlertaDto;
import equipoBlanco.com.prison_service.modules.control.model.Alerta;
import equipoBlanco.com.prison_service.modules.control.repository.AlertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private AlertaService alertaService;

    private Alerta alertaActiva;
    private UUID alertaId;
    private UUID expedienteId;

    @BeforeEach
    void setUp() {
        alertaId    = UUID.randomUUID();
        expedienteId = UUID.randomUUID();

        alertaActiva = Alerta.builder()
                .id(alertaId)
                .nivel(1)
                .expedienteId(expedienteId)
                .nombreEgresado("Juan Pérez")
                .cedulaEgresado("V-12345678")
                .fechaEmision(LocalDateTime.now())
                .destinatario("María González")
                .estado("activa")
                .accionRequerida("Incumplimiento #1 registrado para: Juan Pérez")
                .build();
    }

    // ─── Test 1: Campana filtra alertas activas por destinatario ─────────────

    @Test
    void testObtenerAlertasActivasPorDestinatario() {
        when(alertaRepository.findByDestinatarioAndEstado("María González", "activa"))
                .thenReturn(List.of(alertaActiva));

        List<AlertaDto> resultado = alertaService.obtenerAlertasActivasPorDestinatario("María González");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNivel()).isEqualTo(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("activa");
        assertThat(resultado.get(0).getNombreEgresado()).isEqualTo("Juan Pérez");
        assertThat(resultado.get(0).getCedulaEgresado()).isEqualTo("V-12345678");
        assertThat(resultado.get(0).getExpedienteId()).isEqualTo(expedienteId);

        verify(alertaRepository).findByDestinatarioAndEstado("María González", "activa");
    }

    // ─── Test 2: Atender alerta cambia estado y graba observación ─────────────

    @Test
    void testAtenderAlerta_marcaEstadoYGuardaObservacion() {
        AtenderAlertaDto dto = new AtenderAlertaDto("Se realizó llamada de seguimiento.");

        when(alertaRepository.findById(alertaId)).thenReturn(Optional.of(alertaActiva));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        AlertaDto resultado = alertaService.atenderAlerta(alertaId, dto);

        assertThat(resultado.getEstado()).isEqualTo("atendida");
        assertThat(resultado.getObservacionAtencion()).isEqualTo("Se realizó llamada de seguimiento.");

        verify(alertaRepository).findById(alertaId);
        verify(alertaRepository).save(alertaActiva);
    }

    // ─── Test 3: Atender alerta inexistente lanza excepción ───────────────────

    @Test
    void testAtenderAlerta_noExiste_lanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        AtenderAlertaDto dto = new AtenderAlertaDto(null);

        when(alertaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertaService.atenderAlerta(idInexistente, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Alerta no encontrada");

        verify(alertaRepository, never()).save(any());
    }
}
