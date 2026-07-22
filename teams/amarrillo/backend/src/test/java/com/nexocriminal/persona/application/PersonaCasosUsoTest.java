package com.nexocriminal.persona.application;

import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.domain.port.PersonaRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de los casos de uso del dominio Persona.
 * Se usan mocks del puerto PersonaRepositoryPort (sin base de datos real),
 * posible gracias a la arquitectura hexagonal.
 */
class PersonaCasosUsoTest {

    // ================= CreatePersona (regla: documento unico) =================

    @Test
    @DisplayName("Crear: guarda cuando el documento no existe")
    void crear_guardaCuandoDocumentoNoExiste() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        Persona persona = mock(Persona.class);
        when(persona.getDocumento()).thenReturn("V-12345678");
        when(repo.findByDocumento("V-12345678")).thenReturn(Optional.empty());
        when(repo.save(persona)).thenReturn(persona);
        CreatePersona caso = new CreatePersona(repo);

        Persona resultado = caso.execute(persona);

        assertNotNull(resultado);
        verify(repo).save(persona);
    }

    @Test
    @DisplayName("Crear: lanza excepcion si el documento ya existe (regla de unicidad)")
    void crear_lanzaExcepcionSiDocumentoDuplicado() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        Persona persona = mock(Persona.class);
        Persona existente = mock(Persona.class);
        when(persona.getDocumento()).thenReturn("V-12345678");
        when(repo.findByDocumento("V-12345678")).thenReturn(Optional.of(existente));
        CreatePersona caso = new CreatePersona(repo);

        assertThrows(IllegalArgumentException.class, () -> caso.execute(persona));
        verify(repo, never()).save(any());
    }

    // ================= GetPersona =================

    @Test
    @DisplayName("Obtener: devuelve la persona cuando existe")
    void obtener_devuelveCuandoExiste() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        Persona persona = mock(Persona.class);
        when(repo.findById(1L)).thenReturn(Optional.of(persona));
        GetPersona caso = new GetPersona(repo);

        assertEquals(persona, caso.execute(1L));
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("Obtener: lanza excepcion cuando no existe")
    void obtener_lanzaExcepcionCuandoNoExiste() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        GetPersona caso = new GetPersona(repo);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> caso.execute(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ================= UpdatePersona =================

    @Test
    @DisplayName("Actualizar: modifica y guarda cuando existe")
    void actualizar_modificaYGuarda() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        Persona existente = mock(Persona.class);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);
        UpdatePersona caso = new UpdatePersona(repo);

        Persona resultado = caso.execute(1L, "Maria", "Perez", null, null, null, null);

        assertNotNull(resultado);
        verify(existente).actualizarDatos("Maria", "Perez", null, null, null, null);
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: lanza excepcion cuando no existe")
    void actualizar_lanzaExcepcionCuandoNoExiste() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        UpdatePersona caso = new UpdatePersona(repo);

        assertThrows(IllegalArgumentException.class,
                () -> caso.execute(99L, "Maria", "Perez", null, null, null, null));
        verify(repo, never()).save(any());
    }

    // ================= ListPersonas =================

    @Test
    @DisplayName("Listar: devuelve todas las personas")
    void listar_devuelveTodas() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        when(repo.findAll()).thenReturn(List.of(mock(Persona.class), mock(Persona.class)));
        ListPersonas caso = new ListPersonas(repo);

        assertEquals(2, caso.execute().size());
        verify(repo).findAll();
    }

    // ================= FindIntermediarios =================

    @Test
    @DisplayName("Intermediarios: delega la busqueda al repositorio")
    void intermediarios_delegaAlRepositorio() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        when(repo.findIntermediarios(1L, 2L)).thenReturn(List.of(mock(Persona.class)));
        FindIntermediarios caso = new FindIntermediarios(repo);

        assertEquals(1, caso.execute(1L, 2L).size());
        verify(repo).findIntermediarios(1L, 2L);
    }

    // ================= DeletePersona =================

    @Test
    @DisplayName("Eliminar: delega el borrado al repositorio")
    void eliminar_delegaAlRepositorio() {
        PersonaRepositoryPort repo = mock(PersonaRepositoryPort.class);
        DeletePersona caso = new DeletePersona(repo);

        caso.execute(1L);

        verify(repo).deleteById(1L);
    }
}