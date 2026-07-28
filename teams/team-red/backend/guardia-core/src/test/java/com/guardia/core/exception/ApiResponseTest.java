package com.guardia.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiResponse - Pruebas Unitarias")
class ApiResponseTest {

    @Test
    @DisplayName("ok(data) debe construir una respuesta exitosa sin mensaje")
    void debeConstruirOkSinMensaje() {
        ApiResponse<String> response = ApiResponse.ok("contenido");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isNull();
        assertThat(response.data()).isEqualTo("contenido");
        assertThat(response.errors()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("ok(message, data) debe construir una respuesta exitosa con mensaje")
    void debeConstruirOkConMensaje() {
        ApiResponse<String> response = ApiResponse.ok("Operación exitosa", "contenido");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Operación exitosa");
        assertThat(response.data()).isEqualTo("contenido");
    }

    @Test
    @DisplayName("error(message, errors) debe construir una respuesta fallida con detalle de errores")
    void debeConstruirErrorConDetalle() {
        ApiResponse<Void> response = ApiResponse.error("Error de validación", java.util.List.of("campo: inválido"));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Error de validación");
        assertThat(response.data()).isNull();
        assertThat(response.errors()).isEqualTo(java.util.List.of("campo: inválido"));
    }

    @Test
    @DisplayName("error(message) debe construir una respuesta fallida sin detalle de errores")
    void debeConstruirErrorSinDetalle() {
        ApiResponse<Void> response = ApiResponse.error("Fallo inesperado");

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Fallo inesperado");
        assertThat(response.errors()).isNull();
    }
}
