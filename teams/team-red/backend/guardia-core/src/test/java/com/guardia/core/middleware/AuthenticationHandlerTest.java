package com.guardia.core.middleware;

import com.guardia.core.security.SsoTokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationHandler - Pruebas Unitarias")
class AuthenticationHandlerTest {

    @Mock
    private SsoTokenValidator ssoTokenValidator;

    private AuthenticationHandler handler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        handler = new AuthenticationHandler(ssoTokenValidator);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    @DisplayName("Cuando falta o es inválido el encabezado Authorization")
    class SinAuthorizationValido {

        @Test
        @DisplayName("Debe rechazar con 401 cuando no hay encabezado Authorization")
        void debeRechazarSinEncabezado() throws Exception {
            handler.handle(request, response);

            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(response.getContentAsString()).contains("Debe iniciar sesión");
            verifyNoInteractions(ssoTokenValidator);
        }

        @Test
        @DisplayName("Debe rechazar con 401 cuando el encabezado no tiene el prefijo Bearer")
        void debeRechazarSinPrefijoBearer() throws Exception {
            request.addHeader("Authorization", "Basic abc123");

            handler.handle(request, response);

            assertThat(response.getStatus()).isEqualTo(401);
            verifyNoInteractions(ssoTokenValidator);
        }
    }

    @Test
    @DisplayName("Debe rechazar con 401 cuando el token no es válido según el validador SSO")
    void debeRechazarTokenInvalido() throws Exception {
        request.addHeader("Authorization", "Bearer token-invalido");
        when(ssoTokenValidator.validarYExtraerUsuario("token-invalido")).thenReturn(Optional.empty());

        handler.handle(request, response);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("La sesión no es válida");
    }

    @Test
    @DisplayName("Debe autenticar y registrar el username en el request cuando el token es válido")
    void debeAutenticarConTokenValido() throws Exception {
        request.addHeader("Authorization", "Bearer token-valido");
        when(ssoTokenValidator.validarYExtraerUsuario("token-valido")).thenReturn(Optional.of("jperez"));

        handler.handle(request, response);

        assertThat(request.getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME)).isEqualTo("jperez");
        assertThat(response.getStatus()).isEqualTo(200); // no se modificó: el handler continúa la cadena
    }

    @Test
    @DisplayName("Debe registrar el rol del encabezado X-User-Role cuando viene informado")
    void debeRegistrarRolCuandoViene() throws Exception {
        request.addHeader("Authorization", "Bearer token-valido");
        request.addHeader("X-User-Role", " ANALISTA ");
        when(ssoTokenValidator.validarYExtraerUsuario("token-valido")).thenReturn(Optional.of("jperez"));

        handler.handle(request, response);

        assertThat(request.getAttribute("wife.middleware.user-role")).isEqualTo("ANALISTA");
    }

    @Test
    @DisplayName("La cadena debe continuar hacia el siguiente handler cuando la autenticación es exitosa")
    void debeContinuarLaCadenaCuandoAutenticacionExitosa() throws Exception {
        request.addHeader("Authorization", "Bearer token-valido");
        when(ssoTokenValidator.validarYExtraerUsuario("token-valido")).thenReturn(Optional.of("jperez"));
        RequestHandler siguiente = mock(RequestHandler.class);
        handler.setNext(siguiente);

        handler.handle(request, response);

        verify(siguiente).handle(request, response);
    }

    @Test
    @DisplayName("La cadena NO debe continuar hacia el siguiente handler cuando la autenticación falla")
    void noDebeContinuarLaCadenaCuandoAutenticacionFalla() throws Exception {
        RequestHandler siguiente = mock(RequestHandler.class);
        handler.setNext(siguiente);

        handler.handle(request, response);

        verifyNoInteractions(siguiente);
    }
}
