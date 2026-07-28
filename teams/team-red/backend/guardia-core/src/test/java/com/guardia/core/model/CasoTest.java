package com.guardia.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Caso - Pruebas Unitarias de Modelo")
class CasoTest {

    @Test
    @DisplayName("cantidadExpedientes() debe retornar el tamaño de la lista de expedientes")
    void debeRetornarCantidadDeExpedientes() {
        Caso caso = Caso.builder()
                .expedientes(List.of(new Expediente(), new Expediente()))
                .build();

        assertThat(caso.cantidadExpedientes()).isEqualTo(2);
    }

    @Test
    @DisplayName("cantidadExpedientes() debe retornar 0 cuando la lista está vacía")
    void debeRetornarCeroConListaVacia() {
        Caso caso = Caso.builder().expedientes(new ArrayList<>()).build();

        assertThat(caso.cantidadExpedientes()).isZero();
    }

    @Test
    @DisplayName("cantidadExpedientes() debe retornar 0 cuando la lista es null")
    void debeRetornarCeroConListaNull() {
        Caso caso = new Caso();
        caso.setExpedientes(null);

        assertThat(caso.cantidadExpedientes()).isZero();
    }
}
