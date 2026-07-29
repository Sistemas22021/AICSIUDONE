package com.guardia.core.middleware;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("RequestLoggingHandler - Pruebas Unitarias")
class RequestLoggingHandlerTest {

    @Test
    @DisplayName("Debe continuar siempre la cadena tras registrar la petición")
    void debeContinuarLaCadenaSiempre() throws Exception {
        RequestLoggingHandler handler = new RequestLoggingHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/expedientes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestHandler siguiente = mock(RequestHandler.class);
        handler.setNext(siguiente);

        handler.handle(request, response);

        verify(siguiente).handle(request, response);
    }

    @Test
    @DisplayName("No debe lanzar excepciones cuando no hay un siguiente handler configurado")
    void noDebeFallarSinSiguienteHandler() {
        RequestLoggingHandler handler = new RequestLoggingHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/casos");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(handler).satisfies(h -> h.handle(request, response));
    }
}
