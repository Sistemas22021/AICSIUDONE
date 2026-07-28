package com.guardia.core.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestLoggingInterceptor - Pruebas Unitarias")
class RequestLoggingInterceptorTest {

    private final RequestLoggingInterceptor interceptor = new RequestLoggingInterceptor();
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest("GET", "/api/v1/expedientes");
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("preHandle() siempre debe continuar la cadena y registrar el instante de inicio")
    void preHandleDebeContinuarYRegistrarInicio() {
        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isTrue();
        assertThat(request.getAttribute("__request_start_nanos")).isNotNull();
    }

    @Test
    @DisplayName("afterCompletion() no debe lanzar excepciones para una respuesta 2xx exitosa")
    void afterCompletionConRespuestaExitosa() {
        interceptor.preHandle(request, response, new Object());
        response.setStatus(200);

        assertThat(interceptor).satisfies(i -> i.afterCompletion(request, response, new Object(), null));
    }

    @Test
    @DisplayName("afterCompletion() no debe lanzar excepciones para un error de negocio (422)")
    void afterCompletionConErrorDeNegocio() {
        response.setStatus(422);
        request.setAttribute("wife.logging.error-detail", "Regla de negocio violada");

        assertThat(interceptor).satisfies(i -> i.afterCompletion(request, response, new Object(), null));
    }

    @Test
    @DisplayName("afterCompletion() no debe lanzar excepciones para un error funcional (404)")
    void afterCompletionConErrorFuncional() {
        response.setStatus(404);

        assertThat(interceptor).satisfies(i -> i.afterCompletion(request, response, new Object(), null));
    }

    @Test
    @DisplayName("afterCompletion() no debe lanzar excepciones para un error interno (500), usando el mensaje de la excepción")
    void afterCompletionConErrorInterno() {
        response.setStatus(500);

        assertThat(interceptor).satisfies(i ->
                i.afterCompletion(request, response, new Object(), new RuntimeException("fallo inesperado")));
    }

    @Test
    @DisplayName("afterCompletion() no debe lanzar excepciones cuando no se registró el inicio de la petición")
    void afterCompletionSinInicioRegistrado() {
        response.setStatus(200);

        assertThat(interceptor).satisfies(i -> i.afterCompletion(request, response, new Object(), null));
    }
}
