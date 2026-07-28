// Ruta destino: src/test/java/com/guardia/core/service/UsuarioServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link UsuarioServiceImpl}.
 *
 * <p>La entidad {@code Usuario} refleja la tabla compartida {@code users} del
 * servicio de SSO: id UUID (no autoincremental) y campos username/password/
 * fullName/profilePhotoUrl/rol. Este servicio ya no crea, autentica ni busca
 * usuarios por identificación/correo (eso lo gestiona el auth-service); solo
 * consulta, actualiza datos de perfil y elimina.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl - Pruebas Unitarias")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UUID usuarioId;
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común): usuario persistido de referencia
        usuarioId = UUID.randomUUID();
        usuarioEjemplo = Usuario.builder()
                .id(usuarioId)
                .username("jperez")
                .password("hash-bcrypt")
                .fullName("Juan Perez")
                .profilePhotoUrl("https://cdn.example.com/jperez.png")
                .createdAt(OffsetDateTime.now())
                .rol("OFICIAL")
                .build();
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar el usuario cuando el id existe")
        void debeRetornarUsuarioCuandoExiste() {
            // Arrange
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            UsuarioResponse resultado = usuarioService.obtenerPorId(usuarioId);

            // Assert
            assertThat(resultado.id()).isEqualTo(usuarioId);
            assertThat(resultado.username()).isEqualTo("jperez");
            assertThat(resultado.fullName()).isEqualTo("Juan Perez");
            assertThat(resultado.rol()).isEqualTo("OFICIAL");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el id no existe")
        void debeLanzarExcepcionCuandoIdNoExiste() {
            // Arrange
            UUID inexistente = UUID.randomUUID();
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.obtenerPorId(inexistente))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con id " + inexistente + " no encontrado.");
        }
    }

    @Nested
    @DisplayName("obtenerPorUsername()")
    class ObtenerPorUsername {

        @Test
        @DisplayName("Debe retornar el usuario cuando el username existe")
        void debeRetornarUsuarioCuandoUsernameExiste() {
            // Arrange
            when(usuarioRepository.findByUsername("jperez")).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            UsuarioResponse resultado = usuarioService.obtenerPorUsername("jperez");

            // Assert
            assertThat(resultado.username()).isEqualTo("jperez");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException con mensaje específico cuando no existe")
        void debeLanzarExcepcionCuandoUsernameNoExiste() {
            // Arrange
            when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.obtenerPorUsername("fantasma"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con username 'fantasma' no encontrado.");
        }
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar la lista completa mapeada a UsuarioResponse")
    void debeRetornarTodosLosUsuarios() {
        // Arrange
        Usuario usuario2 = Usuario.builder().id(UUID.randomUUID()).username("adiaz")
                .password("hash2").fullName("Ana Diaz").rol("ANALISTA").build();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioEjemplo, usuario2));

        // Act
        List<UsuarioResponse> resultado = usuarioService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(UsuarioResponse::fullName)
                .containsExactly("Juan Perez", "Ana Diaz");
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar el nombre completo y la foto de perfil cuando ambos vienen informados")
        void debeActualizarUsuarioExistente() {
            // Arrange
            UsuarioRequest requestActualizado = new UsuarioRequest(
                    "jperez", "clave-no-usada", "Juan Perez Actualizado", "https://cdn.example.com/nueva.png");
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            UsuarioResponse resultado = usuarioService.actualizar(usuarioId, requestActualizado);

            // Assert
            assertThat(resultado.fullName()).isEqualTo("Juan Perez Actualizado");
            assertThat(resultado.profilePhotoUrl()).isEqualTo("https://cdn.example.com/nueva.png");
            verify(usuarioRepository).save(usuarioEjemplo);
        }

        @Test
        @DisplayName("Debe conservar el nombre completo actual cuando el nuevo viene en blanco")
        void debeConservarNombreCuandoVieneEnBlanco() {
            // Arrange
            UsuarioRequest requestActualizado = new UsuarioRequest("jperez", "clave-no-usada", "   ", null);
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            UsuarioResponse resultado = usuarioService.actualizar(usuarioId, requestActualizado);

            // Assert: al ser blank, no se sobreescribe el fullName original
            assertThat(resultado.fullName()).isEqualTo("Juan Perez");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al actualizar un usuario inexistente")
        void debeLanzarExcepcionAlActualizarUsuarioInexistente() {
            // Arrange
            UUID inexistente = UUID.randomUUID();
            UsuarioRequest request = new UsuarioRequest("x", "clavesegura", "Nombre", null);
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.actualizar(inexistente, request))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar el usuario cuando existe")
        void debeEliminarUsuarioExistente() {
            // Arrange
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            usuarioService.eliminar(usuarioId);

            // Assert
            verify(usuarioRepository, times(1)).deleteById(usuarioId);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminarUsuarioInexistente() {
            // Arrange
            UUID inexistente = UUID.randomUUID();
            when(usuarioRepository.findById(inexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.eliminar(inexistente))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(usuarioRepository, never()).deleteById(any());
        }
    }
}
