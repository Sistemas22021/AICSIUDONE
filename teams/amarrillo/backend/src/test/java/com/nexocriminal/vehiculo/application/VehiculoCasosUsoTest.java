package com.nexocriminal.vehiculo.application;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.VehiculoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de los casos de uso del dominio Vehiculo.
 */
class VehiculoCasosUsoTest {

    // ================= CreateVehiculo (regla: placa unica) =================

    @Test
    @DisplayName("Crear: guarda cuando la placa no existe")
    void crear_guardaCuandoPlacaNoExiste() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        Vehiculo vehiculo = mock(Vehiculo.class);
        when(vehiculo.getPlaca()).thenReturn("AB123CD");
        when(repo.findByPlaca("AB123CD")).thenReturn(Optional.empty());
        when(repo.save(vehiculo)).thenReturn(vehiculo);
        CreateVehiculo caso = new CreateVehiculo(repo);

        assertNotNull(caso.execute(vehiculo));
        verify(repo).save(vehiculo);
    }

    @Test
    @DisplayName("Crear: lanza excepcion si la placa ya existe (regla de unicidad)")
    void crear_lanzaExcepcionSiPlacaDuplicada() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        Vehiculo vehiculo = mock(Vehiculo.class);
        when(vehiculo.getPlaca()).thenReturn("AB123CD");
        when(repo.findByPlaca("AB123CD")).thenReturn(Optional.of(mock(Vehiculo.class)));
        CreateVehiculo caso = new CreateVehiculo(repo);

        assertThrows(IllegalArgumentException.class, () -> caso.execute(vehiculo));
        verify(repo, never()).save(any());
    }

    // ================= GetVehiculo =================

    @Test
    @DisplayName("Obtener: devuelve el vehiculo cuando existe")
    void obtener_devuelveCuandoExiste() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        Vehiculo vehiculo = mock(Vehiculo.class);
        when(repo.findById(1L)).thenReturn(Optional.of(vehiculo));
        GetVehiculo caso = new GetVehiculo(repo);

        assertEquals(vehiculo, caso.execute(1L));
    }

    @Test
    @DisplayName("Obtener: lanza excepcion cuando no existe")
    void obtener_lanzaExcepcionCuandoNoExiste() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        GetVehiculo caso = new GetVehiculo(repo);

        assertThrows(IllegalArgumentException.class, () -> caso.execute(99L));
    }

    // ================= UpdateVehiculo =================

    @Test
    @DisplayName("Actualizar: modifica y guarda cuando existe")
    void actualizar_modificaYGuarda() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        Vehiculo existente = mock(Vehiculo.class);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);
        UpdateVehiculo caso = new UpdateVehiculo(repo);

        caso.execute(1L, "Toyota", "Corolla", 2019, "Gris", EstadoVehiculo.NORMAL, null);

        verify(existente).actualizarDatos("Toyota", "Corolla", 2019, "Gris", EstadoVehiculo.NORMAL, null);
        verify(repo).save(existente);
    }

    // ================= ChangeVehiculoEstado =================

    @Test
    @DisplayName("Cambiar estado: actualiza el estado y guarda")
    void cambiarEstado_actualizaYGuarda() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        Vehiculo existente = mock(Vehiculo.class);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);
        ChangeVehiculoEstado caso = new ChangeVehiculoEstado(repo);

        caso.execute(1L, EstadoVehiculo.ROBADO);

        verify(existente).cambiarEstado(EstadoVehiculo.ROBADO);
        verify(repo).save(existente);
    }

    // ================= ListVehiculos =================

    @Test
    @DisplayName("Listar: sin estado devuelve todos")
    void listar_sinEstadoDevuelveTodos() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        when(repo.findAll()).thenReturn(List.of(mock(Vehiculo.class)));
        ListVehiculos caso = new ListVehiculos(repo);

        assertEquals(1, caso.execute(null).size());
        verify(repo).findAll();
        verify(repo, never()).findByEstado(any());
    }

    @Test
    @DisplayName("Listar: con estado filtra por estado")
    void listar_conEstadoFiltra() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        when(repo.findByEstado(EstadoVehiculo.ROBADO)).thenReturn(List.of(mock(Vehiculo.class)));
        ListVehiculos caso = new ListVehiculos(repo);

        assertEquals(1, caso.execute(EstadoVehiculo.ROBADO).size());
        verify(repo).findByEstado(EstadoVehiculo.ROBADO);
        verify(repo, never()).findAll();
    }

    // ================= DeleteVehiculo =================

    @Test
    @DisplayName("Eliminar: delega el borrado al repositorio")
    void eliminar_delegaAlRepositorio() {
        VehiculoRepositoryPort repo = mock(VehiculoRepositoryPort.class);
        DeleteVehiculo caso = new DeleteVehiculo(repo);

        caso.execute(1L);

        verify(repo).deleteById(1L);
    }
}