// Ruta destino: src/test/java/com/guardia/core/service/UsuarioServiceImplTest.java
package com.guardia.core.service;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link UsuarioServiceImpl}.
 * Aísla la capa de servicio mockeando {@link UsuarioRepository}, sin levantar contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl - Pruebas Unitarias")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioEjemplo;
    private UsuarioRequest requestEjemplo;

    @BeforeEach
    void setUp() {
        // Arrange (fixture común): usuario persistido de referencia
        usuarioEjemplo = Usuario.builder()
                .id(1L)
                .nombre("Juan Perez")
                .identificacion("V-12345678")
                .credenciales("clave-secreta")
                .correo("juan.perez@guardia.com")
                .build();

        requestEjemplo = new UsuarioRequest(
                "Juan Perez",
                "V-12345678",
                "clave-secreta",
                "juan.perez@guardia.com"
        );
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear un usuario exitosamente cuando identificación y correo son únicos")
        void debeCrearUsuarioExitosamente() {
            // Arrange
            when(usuarioRepository.existsByIdentificacion(requestEjemplo.identificacion())).thenReturn(false);
            when(usuarioRepository.existsByCorreo(requestEjemplo.correo())).thenReturn(false);
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

            // Act
            UsuarioResponse resultado = usuarioService.crear(requestEjemplo);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nombre()).isEqualTo("Juan Perez");
            assertThat(resultado.identificacion()).isEqualTo("V-12345678");
            assertThat(resultado.correo()).isEqualTo("juan.perez@guardia.com");
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando la identificación ya existe")
        void debeLanzarExcepcionCuandoIdentificacionYaExiste() {
            // Arrange
            when(usuarioRepository.existsByIdentificacion(requestEjemplo.identificacion())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.crear(requestEjemplo))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Ya existe un usuario con esa identificación.");

            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException cuando el correo ya existe")
        void debeLanzarExcepcionCuandoCorreoYaExiste() {
            // Arrange
            when(usuarioRepository.existsByIdentificacion(requestEjemplo.identificacion())).thenReturn(false);
            when(usuarioRepository.existsByCorreo(requestEjemplo.correo())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.crear(requestEjemplo))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Ya existe un usuario con ese correo.");

            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar el usuario cuando el id existe")
        void debeRetornarUsuarioCuandoExiste() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            UsuarioResponse resultado = usuarioService.obtenerPorId(1L);

            // Assert
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nombre()).isEqualTo("Juan Perez");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el id no existe")
        void debeLanzarExcepcionCuandoIdNoExiste() {
            // Arrange
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con id 99 no encontrado.");
        }
    }

    @Nested
    @DisplayName("obtenerPorIdentificacion()")
    class ObtenerPorIdentificacion {

        @Test
        @DisplayName("Debe retornar el usuario cuando la identificación existe")
        void debeRetornarUsuarioCuandoIdentificacionExiste() {
            // Arrange
            when(usuarioRepository.findByIdentificacion("V-12345678")).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            UsuarioResponse resultado = usuarioService.obtenerPorIdentificacion("V-12345678");

            // Assert
            assertThat(resultado.identificacion()).isEqualTo("V-12345678");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException con mensaje específico cuando no existe")
        void debeLanzarExcepcionCuandoIdentificacionNoExiste() {
            // Arrange
            when(usuarioRepository.findByIdentificacion("X-000")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.obtenerPorIdentificacion("X-000"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con identificación X-000 no encontrado.");
        }
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar la lista completa mapeada a UsuarioResponse")
    void debeRetornarTodosLosUsuarios() {
        // Arrange
        Usuario usuario2 = Usuario.builder().id(2L).nombre("Ana Diaz")
                .identificacion("V-8888").credenciales("clave2").correo("ana@guardia.com").build();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioEjemplo, usuario2));

        // Act
        List<UsuarioResponse> resultado = usuarioService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(UsuarioResponse::nombre)
                .containsExactly("Juan Perez", "Ana Diaz");
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar los datos del usuario existente")
        void debeActualizarUsuarioExistente() {
            // Arrange
            UsuarioRequest requestActualizado = new UsuarioRequest(
                    "Juan Perez Actualizado", "V-12345678", "nueva-clave", "nuevo@guardia.com");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            UsuarioResponse resultado = usuarioService.actualizar(1L, requestActualizado);

            // Assert
            assertThat(resultado.nombre()).isEqualTo("Juan Perez Actualizado");
            assertThat(resultado.correo()).isEqualTo("nuevo@guardia.com");
            verify(usuarioRepository).save(usuarioEjemplo);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al actualizar un usuario inexistente")
        void debeLanzarExcepcionAlActualizarUsuarioInexistente() {
            // Arrange
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.actualizar(99L, requestEjemplo))
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
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            usuarioService.eliminar(1L);

            // Assert
            verify(usuarioRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException y no eliminar cuando no existe")
        void debeLanzarExcepcionAlEliminarUsuarioInexistente() {
            // Arrange
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(usuarioRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("autenticar()")
    class Autenticar {

        @Test
        @DisplayName("Debe retornar true cuando las credenciales coinciden")
        void debeRetornarTrueCuandoCredencialesCoinciden() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            boolean resultado = usuarioService.autenticar(1L, "clave-secreta");

            // Assert
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando las credenciales no coinciden")
        void debeRetornarFalseCuandoCredencialesNoCoinciden() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            boolean resultado = usuarioService.autenticar(1L, "clave-incorrecta");

            // Assert
            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("obtenerPorCorreo()")
    class ObtenerPorCorreo {

        @Test
        @DisplayName("Debe retornar el usuario cuando el correo existe")
        void debeRetornarUsuarioCuandoCorreoExiste() {
            // Arrange
            when(usuarioRepository.findByCorreo("juan.perez@guardia.com")).thenReturn(Optional.of(usuarioEjemplo));

            // Act
            UsuarioResponse resultado = usuarioService.obtenerPorCorreo("juan.perez@guardia.com");

            // Assert
            assertThat(resultado.correo()).isEqualTo("juan.perez@guardia.com");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el correo no existe")
        void debeLanzarExcepcionCuandoCorreoNoExiste() {
            // Arrange
            when(usuarioRepository.findByCorreo("noexiste@guardia.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> usuarioService.obtenerPorCorreo("noexiste@guardia.com"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuario con correo noexiste@guardia.com no encontrado.");
        }
    }
}
