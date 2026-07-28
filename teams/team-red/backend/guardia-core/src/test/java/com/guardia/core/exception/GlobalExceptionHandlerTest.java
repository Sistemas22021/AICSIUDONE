package com.guardia.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler - Pruebas Unitarias")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/expedientes/1");

    @SuppressWarnings("unused")
    private void metodoSenuelo(String parametro) {
    }

    @Test
    @DisplayName("handleNotFound() debe responder 404 con el mensaje de la excepción")
    void debeManejarResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Expediente", 99L);

        ResponseEntity<ApiResponse<Void>> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().message()).isEqualTo("Expediente con id 99 no encontrado.");
    }

    @Test
    @DisplayName("handleBusiness() debe responder 422 con el mensaje de la excepción")
    void debeManejarBusinessException() {
        BusinessException ex = new BusinessException("El expediente ya está sellado.");

        ResponseEntity<ApiResponse<Void>> response = handler.handleBusiness(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().message()).isEqualTo("El expediente ya está sellado.");
    }

    @Test
    @DisplayName("handleValidation() debe responder 400 con la lista de errores de campo")
    void debeManejarMethodArgumentNotValidException() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("metodoSenuelo", String.class), 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objetivo");
        bindingResult.addError(new FieldError("objetivo", "descripcion", "no puede estar vacío"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Error de validación");
        assertThat(response.getBody().errors()).asList()
                .containsExactly("descripcion: no puede estar vacío");
    }

    @Test
    @DisplayName("handleGeneral() debe responder 500 incluyendo el mensaje original de la excepción")
    void debeManejarExcepcionGeneral() {
        Exception ex = new RuntimeException("Fallo de conexión con la base de datos");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message())
                .isEqualTo("Error interno del servidor: Fallo de conexión con la base de datos");
    }
}
