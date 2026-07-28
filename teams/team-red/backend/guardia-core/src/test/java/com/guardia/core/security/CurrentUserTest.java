package com.guardia.core.security;

import com.guardia.core.middleware.AuthenticationHandler;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link CurrentUser}. Simula el ciclo de vida de una
 * petición HTTP mediante {@link ServletRequestAttributes} para poder ejercitar
 * la lectura del atributo poblado por {@link AuthenticationHandler} sin
 * necesidad de levantar un contexto web completo.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CurrentUser - Pruebas Unitarias")
class CurrentUserTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CurrentUser currentUser;

    @AfterEach
    void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    private void simularPeticionAutenticada(String username) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (username != null) {
            request.setAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME, username);
        }
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("username()")
    class Username {

        @Test
        @DisplayName("Debe retornar vacío cuando no hay una petición HTTP en curso")
        void debeRetornarVacioSinPeticion() {
            RequestContextHolder.resetRequestAttributes();
            assertThat(currentUser.username()).isEmpty();
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la petición no tiene el atributo de autenticación")
        void debeRetornarVacioSinAtributo() {
            simularPeticionAutenticada(null);
            assertThat(currentUser.username()).isEmpty();
        }

        @Test
        @DisplayName("Debe retornar el username cuando el atributo fue poblado por AuthenticationHandler")
        void debeRetornarUsernameCuandoPresente() {
            simularPeticionAutenticada("jperez");
            assertThat(currentUser.username()).contains("jperez");
        }

        @Test
        @DisplayName("Debe retornar vacío cuando el atributo está en blanco")
        void debeRetornarVacioConAtributoEnBlanco() {
            simularPeticionAutenticada("   ");
            assertThat(currentUser.username()).isEmpty();
        }
    }

    @Nested
    @DisplayName("usuario()")
    class UsuarioMetodo {

        @Test
        @DisplayName("Debe retornar el usuario cuando hay username y el repositorio lo encuentra")
        void debeRetornarUsuarioCuandoExiste() {
            simularPeticionAutenticada("jperez");
            Usuario usuario = Usuario.builder().username("jperez").build();
            when(usuarioRepository.findByUsername("jperez")).thenReturn(Optional.of(usuario));

            assertThat(currentUser.usuario()).contains(usuario);
        }

        @Test
        @DisplayName("Debe retornar vacío cuando no hay petición en curso (sin consultar el repositorio)")
        void debeRetornarVacioSinPeticion() {
            RequestContextHolder.resetRequestAttributes();
            assertThat(currentUser.usuario()).isEmpty();
        }

        @Test
        @DisplayName("Debe retornar vacío cuando el repositorio no encuentra al usuario autenticado")
        void debeRetornarVacioCuandoRepositorioNoEncuentra() {
            simularPeticionAutenticada("fantasma");
            when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

            assertThat(currentUser.usuario()).isEmpty();
        }
    }
}
