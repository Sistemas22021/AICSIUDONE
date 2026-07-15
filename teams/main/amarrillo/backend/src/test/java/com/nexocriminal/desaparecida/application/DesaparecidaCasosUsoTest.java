package com.nexocriminal.desaparecida.application;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.DesaparecidaRepositoryPort;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de los casos de uso del dominio Desaparecida.
 *
 * Se prueban en aislamiento usando un mock del puerto DesaparecidaRepositoryPort,
 * sin base de datos real. Esto es posible gracias a la arquitectura hexagonal:
 * los casos de uso dependen de la abstraccion (el puerto), no de JPA.
 */
class DesaparecidaCasosUsoTest {

    /** Helper: crea una desaparecida de prueba con datos minimos. */
    private Desaparecida desaparecidaDePrueba() {
        return Desaparecida.crear(
                "V-12345678", "Ana", "Perez", null,
                null, null, null, null, null, null, null, null, null,
                LocalDateTime.now(), null, null, null, null, null,
                EstadoDesaparicion.BUSCADA, PrioridadDesaparicion.MEDIA);
    }

    // ================= CreateDesaparecida =================

    @Test
    @DisplayName("Crear: guarda la desaparecida en el repositorio y la devuelve")
    void crear_guardaYDevuelve() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        Desaparecida nueva = desaparecidaDePrueba();
        when(repo.save(nueva)).thenReturn(nueva);
        CreateDesaparecida caso = new CreateDesaparecida(repo);

        // Act
        Desaparecida resultado = caso.execute(nueva);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        verify(repo).save(nueva);
    }

    // ================= GetDesaparecida =================

    @Test
    @DisplayName("Obtener: devuelve la desaparecida cuando existe")
    void obtener_devuelveCuandoExiste() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        Desaparecida esperada = desaparecidaDePrueba();
        when(repo.findById(1L)).thenReturn(Optional.of(esperada));
        GetDesaparecida caso = new GetDesaparecida(repo);

        // Act
        Desaparecida resultado = caso.execute(1L);

        // Assert
        assertEquals("Perez", resultado.getApellido());
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("Obtener: lanza excepcion cuando no existe")
    void obtener_lanzaExcepcionCuandoNoExiste() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        GetDesaparecida caso = new GetDesaparecida(repo);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> caso.execute(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ================= UpdateDesaparecida =================

    @Test
    @DisplayName("Actualizar: modifica los datos y guarda cuando existe")
    void actualizar_modificaYGuarda() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        Desaparecida existente = desaparecidaDePrueba();
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Desaparecida.class))).thenAnswer(inv -> inv.getArgument(0));
        UpdateDesaparecida caso = new UpdateDesaparecida(repo);

        // Act: cambiamos el nombre a "Maria"
        Desaparecida resultado = caso.execute(
                1L, "Maria", "Perez", null, null, null, null, null, null, null, null,
                null, null, LocalDateTime.now(), null, null, null, null, null,
                EstadoDesaparicion.BUSCADA, PrioridadDesaparicion.ALTA);

        // Assert
        assertEquals("Maria", resultado.getNombre());
        assertEquals(PrioridadDesaparicion.ALTA, resultado.getPrioridad());
        verify(repo).findById(1L);
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: lanza excepcion cuando la desaparecida no existe")
    void actualizar_lanzaExcepcionCuandoNoExiste() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        UpdateDesaparecida caso = new UpdateDesaparecida(repo);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> caso.execute(
                99L, "Maria", "Perez", null, null, null, null, null, null, null, null,
                null, null, LocalDateTime.now(), null, null, null, null, null,
                EstadoDesaparicion.BUSCADA, PrioridadDesaparicion.ALTA));
        verify(repo, never()).save(any());
    }

    // ================= ChangeDesaparecidaEstado =================

    @Test
    @DisplayName("Cambiar estado: actualiza el estado y guarda")
    void cambiarEstado_actualizaYGuarda() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        Desaparecida existente = desaparecidaDePrueba();
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Desaparecida.class))).thenAnswer(inv -> inv.getArgument(0));
        ChangeDesaparecidaEstado caso = new ChangeDesaparecidaEstado(repo);

        // Act
        Desaparecida resultado = caso.execute(1L, EstadoDesaparicion.ENCONTRADA_VIVA);

        // Assert
        assertEquals(EstadoDesaparicion.ENCONTRADA_VIVA, resultado.getEstado());
        verify(repo).save(existente);
    }

    // ================= ListDesaparecidas =================

    @Test
    @DisplayName("Listar: sin filtros devuelve todas")
    void listar_sinFiltrosDevuelveTodas() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        when(repo.findAll()).thenReturn(List.of(desaparecidaDePrueba()));
        ListDesaparecidas caso = new ListDesaparecidas(repo);

        // Act
        List<Desaparecida> resultado = caso.execute(null, null);

        // Assert
        assertEquals(1, resultado.size());
        verify(repo).findAll();
        verify(repo, never()).findByEstado(any());
    }

    @Test
    @DisplayName("Listar: con estado filtra por estado")
    void listar_conEstadoFiltra() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        when(repo.findByEstado(EstadoDesaparicion.BUSCADA))
                .thenReturn(List.of(desaparecidaDePrueba()));
        ListDesaparecidas caso = new ListDesaparecidas(repo);

        // Act
        List<Desaparecida> resultado = caso.execute(EstadoDesaparicion.BUSCADA, null);

        // Assert
        assertEquals(1, resultado.size());
        verify(repo).findByEstado(EstadoDesaparicion.BUSCADA);
        verify(repo, never()).findAll();
    }

    // ================= DeleteDesaparecida =================

    @Test
    @DisplayName("Eliminar: delega el borrado al repositorio")
    void eliminar_delegaAlRepositorio() {
        // Arrange
        DesaparecidaRepositoryPort repo = mock(DesaparecidaRepositoryPort.class);
        DeleteDesaparecida caso = new DeleteDesaparecida(repo);

        // Act
        caso.execute(1L);

        // Assert
        verify(repo).deleteById(1L);
    }
}