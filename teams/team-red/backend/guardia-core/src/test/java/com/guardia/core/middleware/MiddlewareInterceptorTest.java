package com.guardia.core.middleware;

import com.guardia.core.security.SsoTokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MiddlewareInterceptor - Pruebas Unitarias")
class MiddlewareInterceptorTest {

    @Mock private SsoTokenValidator ssoTokenValidator;
    @Mock private RoleValidationHandler roleValidationHandler;

    private MiddlewareInterceptor interceptor;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new MiddlewareInterceptor(ssoTokenValidator, roleValidationHandler);
        response = new MockHttpServletResponse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/swagger", "/swagger/index.html", "/swagger-ui/index.html",
            "/swagger-ui.html", "/api-docs/openapi.json", "/v3/api-docs"})
    @DisplayName("Debe permitir sin autenticación las rutas de documentación (Swagger/OpenAPI)")
    void debePermitirRutasDeDocumentacion(String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isTrue();
        verifyNoInteractions(ssoTokenValidator, roleValidationHandler);
    }

    @Test
    @DisplayName("Debe responder 200 sin continuar para peticiones OPTIONS (preflight CORS)")
    void debeResponderOkParaOptions() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/expedientes");

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getStatus()).isEqualTo(200);
        verifyNoInteractions(ssoTokenValidator, roleValidationHandler);
    }

    @Test
    @DisplayName("Debe rechazar con 401 cuando falta el encabezado Authorization")
    void debeRechazarSinAuthorization() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/expedientes");

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Debe iniciar sesión");
    }

    @Test
    @DisplayName("Debe rechazar con 401 cuando el token no es válido")
    void debeRechazarTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/expedientes");
        request.addHeader("Authorization", "Bearer invalido");
        when(ssoTokenValidator.validarYExtraerUsuario("invalido")).thenReturn(Optional.empty());

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getContentAsString()).contains("La sesión no es válida");
    }

    @Test
    @DisplayName("Debe delegar en RoleValidationHandler tras autenticar correctamente")
    void debeDelegarEnRoleValidationHandler() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/expedientes");
        request.addHeader("Authorization", "Bearer valido");
        when(ssoTokenValidator.validarYExtraerUsuario("valido")).thenReturn(Optional.of("jperez"));
        when(roleValidationHandler.preHandle(any(), any(), any())).thenReturn(true);

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isTrue();
        assertThat(request.getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME)).isEqualTo("jperez");
        verify(roleValidationHandler).preHandle(eq(request), eq(response), any());
    }

    @Test
    @DisplayName("Debe propagar el resultado negativo de RoleValidationHandler")
    void debePropagarRechazoDeRoleValidationHandler() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/escenas");
        request.addHeader("Authorization", "Bearer valido");
        when(ssoTokenValidator.validarYExtraerUsuario("valido")).thenReturn(Optional.of("jperez"));
        when(roleValidationHandler.preHandle(any(), any(), any())).thenReturn(false);

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("afterCompletion() no debe lanzar excepciones")
    void afterCompletionNoDebeFallar() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/expedientes");
        assertThat(interceptor).satisfies(i -> i.afterCompletion(request, response, new Object(), null));
    }
}
