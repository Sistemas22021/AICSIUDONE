package com.nexocriminal.alerta.application;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.alerta.domain.port.AlertaRepositoryPort;
import com.nexocriminal.domain.alerta.EstadoAlerta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de los casos de uso del dominio Alerta.
 */
class AlertaCasosUsoTest {

    // ================= ChangeAlertaEstado =================

    @Test
    @DisplayName("Cambiar estado: actualiza el estado y guarda cuando existe")
    void cambiarEstado_actualizaYGuarda() {
        AlertaRepositoryPort repo = mock(AlertaRepositoryPort.class);
        Alerta existente = mock(Alerta.class);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);
        ChangeAlertaEstado caso = new ChangeAlertaEstado(repo);

        caso.execute(1L, EstadoAlerta.EN_REVISION);

        verify(existente).cambiarEstado(EstadoAlerta.EN_REVISION);
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Cambiar estado: lanza excepcion cuando la alerta no existe")
    void cambiarEstado_lanzaExcepcionCuandoNoExiste() {
        AlertaRepositoryPort repo = mock(AlertaRepositoryPort.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        ChangeAlertaEstado caso = new ChangeAlertaEstado(repo);

        assertThrows(IllegalArgumentException.class,
                () -> caso.execute(99L, EstadoAlerta.EN_REVISION));
        verify(repo, never()).save(any());
    }

    // ================= ListAlertas =================

    @Test
    @DisplayName("Listar: devuelve todas las alertas")
    void listar_devuelveTodas() {
        AlertaRepositoryPort repo = mock(AlertaRepositoryPort.class);
        when(repo.findAll()).thenReturn(List.of(mock(Alerta.class), mock(Alerta.class)));
        ListAlertas caso = new ListAlertas(repo);

        assertEquals(2, caso.execute().size());
        verify(repo).findAll();
    }

    // ================= ListAlertasPendientes =================

    @Test
    @DisplayName("Listar pendientes: filtra por estado PENDIENTE")
    void listarPendientes_filtraPorEstado() {
        AlertaRepositoryPort repo = mock(AlertaRepositoryPort.class);
        when(repo.findByEstado(EstadoAlerta.PENDIENTE)).thenReturn(List.of(mock(Alerta.class)));
        ListAlertasPendientes caso = new ListAlertasPendientes(repo);

        assertEquals(1, caso.execute().size());
        verify(repo).findByEstado(EstadoAlerta.PENDIENTE);
    }
}