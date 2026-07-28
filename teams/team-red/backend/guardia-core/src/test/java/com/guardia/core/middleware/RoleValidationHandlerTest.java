package com.guardia.core.middleware;

import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link RoleValidationHandler}: la matriz de permisos
 * por rol (OFICIAL / ANALISTA) sobre cada recurso de la API.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleValidationHandler - Pruebas Unitarias")
class RoleValidationHandlerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RoleValidationHandler handler;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
    }

    private MockHttpServletRequest requestDe(String username, String metodo, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(metodo, uri);
        if (username != null) {
            request.setAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME, username);
        }
        return request;
    }

    private void conRol(String username, String rol) {
        Usuario usuario = Usuario.builder().username(username).rol(rol).build();
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
    }

    @Test
    @DisplayName("Debe permitir siempre las peticiones OPTIONS (preflight CORS)")
    void debePermitirOptions() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/expedientes");

        boolean resultado = handler.preHandle(request, response, new Object());

        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar con 401 cuando no hay username autenticado en el request")
    void debeRechazarSinUsername() throws Exception {
        MockHttpServletRequest request = requestDe(null, "GET", "/api/v1/expedientes");

        boolean resultado = handler.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("Usuario no autenticado");
    }

    @Test
    @DisplayName("Debe rechazar con 403 cuando el usuario autenticado no existe en la base de datos")
    void debeRechazarUsuarioInexistente() throws Exception {
        MockHttpServletRequest request = requestDe("fantasma", "GET", "/api/v1/expedientes");
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        boolean resultado = handler.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getContentAsString()).contains("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debe rechazar con un mensaje genérico cuando el rol no es OFICIAL ni ANALISTA")
    void debeRechazarRolDesconocido() throws Exception {
        MockHttpServletRequest request = requestDe("x", "GET", "/api/v1/expedientes");
        conRol("x", "SUPERVISOR");

        boolean resultado = handler.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getContentAsString()).contains("No tiene permisos");
    }

    @Test
    @DisplayName("Debe registrar el rol resuelto como atributo del request tras una autorización exitosa")
    void debeRegistrarRolComoAtributo() throws Exception {
        MockHttpServletRequest request = requestDe("oficial1", "GET", "/api/v1/expedientes");
        conRol("oficial1", "OFICIAL");

        handler.preHandle(request, response, new Object());

        assertThat(request.getAttribute("X-User-Role")).isEqualTo("OFICIAL");
    }

    @Nested
    @DisplayName("Rol OFICIAL")
    class RolOficial {

        @ParameterizedTest(name = "{0} {1} debe permitirse")
        @CsvSource({
                "GET,    /api/v1/expedientes",
                "POST,   /api/v1/expedientes",
                "POST,   /api/v1/incidentes",
                "PATCH,  /api/v1/expedientes/5/sellar",
                "GET,    /api/v1/casos",
                "GET,    /api/v1/delitos/categorias",
                "GET,    /api/v1/tipos-delito",
                "GET,    /api/v1/subtipos-delito",
                "GET,    /api/v1/usuarios",
                "GET,    /api/v1/localizaciones",
                "GET,    /api/v1/involucrados",
        })
        @DisplayName("Operaciones permitidas para OFICIAL")
        void debePermitirOperacionesDeOficial(String metodo, String uri) throws Exception {
            MockHttpServletRequest request = requestDe("of1", metodo, uri);
            conRol("of1", "OFICIAL");

            assertThat(handler.preHandle(request, response, new Object())).isTrue();
        }

        @ParameterizedTest(name = "{0} {1} debe rechazarse")
        @CsvSource({
                "GET,   /api/v1/escenas",
                "POST,  /api/v1/evidencias",
                "GET,   /api/v1/modus-operandi",
                "PUT,   /api/v1/expedientes/5",
        })
        @DisplayName("Operaciones fuera del alcance de OFICIAL")
        void debeRechazarOperacionesFueraDeAlcance(String metodo, String uri) throws Exception {
            MockHttpServletRequest request = requestDe("of1", metodo, uri);
            conRol("of1", "OFICIAL");

            boolean resultado = handler.preHandle(request, response, new Object());

            assertThat(resultado).isFalse();
            assertThat(response.getContentAsString()).contains("Los Oficiales solo pueden");
        }
    }

    @Nested
    @DisplayName("Rol ANALISTA")
    class RolAnalista {

        @ParameterizedTest(name = "{0} {1} debe permitirse")
        @CsvSource({
                "POST,  /api/v1/escenas",
                "DELETE,/api/v1/escenas/1",
                "POST,  /api/v1/escenas-negativas",
                "PUT,   /api/v1/evidencias/1",
                "POST,  /api/v1/modus-operandi",
                "POST,  /api/v1/expedientes/5/reanalizar-mo",
                "POST,  /api/v1/propuestas-mo/1/aprobar",
                "GET,   /api/v1/expedientes",
                "GET,   /api/v1/casos",
                "GET,   /api/v1/delitos/categorias",
                "GET,   /api/v1/tipos-delito",
                "GET,   /api/v1/subtipos-delito",
                "GET,   /api/v1/usuarios",
                "GET,   /api/v1/localizaciones",
                "GET,   /api/v1/involucrados",
                "POST,  /api/v1/casos",
        })
        @DisplayName("Operaciones permitidas para ANALISTA")
        void debePermitirOperacionesDeAnalista(String metodo, String uri) throws Exception {
            MockHttpServletRequest request = requestDe("an1", metodo, uri);
            conRol("an1", "ANALISTA");

            assertThat(handler.preHandle(request, response, new Object())).isTrue();
        }

        @ParameterizedTest(name = "{0} {1} debe rechazarse")
        @CsvSource({
                "POST,  /api/v1/expedientes",
                "POST,  /api/v1/incidentes",
                "PATCH, /api/v1/expedientes/5/sellar",
                "PUT,   /api/v1/casos/1",
                "PUT,   /api/v1/usuarios/1",
        })
        @DisplayName("Operaciones prohibidas explícitamente para ANALISTA")
        void debeRechazarOperacionesProhibidas(String metodo, String uri) throws Exception {
            MockHttpServletRequest request = requestDe("an1", metodo, uri);
            conRol("an1", "ANALISTA");

            boolean resultado = handler.preHandle(request, response, new Object());

            assertThat(resultado).isFalse();
            assertThat(response.getContentAsString()).contains("Los Analistas solo pueden");
        }

        @Test
        @DisplayName("Debe permitir por defecto (fallback) una ruta no listada explícitamente")
        void debePermitirRutaNoListadaPorDefecto() throws Exception {
            MockHttpServletRequest request = requestDe("an1", "GET", "/api/v1/reportes-nuevos");
            conRol("an1", "ANALISTA");

            assertThat(handler.preHandle(request, response, new Object())).isTrue();
        }

        @Test
        @DisplayName("La comparación de rol en el encabezado no debe distinguir mayúsculas/minúsculas")
        void debeSerInsensibleAMayusculas() throws Exception {
            MockHttpServletRequest request = requestDe("an1", "GET", "/api/v1/expedientes");
            conRol("an1", "analista");

            assertThat(handler.preHandle(request, response, new Object())).isTrue();
        }
    }
}
