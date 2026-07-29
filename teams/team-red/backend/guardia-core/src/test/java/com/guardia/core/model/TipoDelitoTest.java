package com.guardia.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TipoDelito - Pruebas Unitarias de Modelo")
class TipoDelitoTest {

    private TipoDelito tipoDelito;

    @BeforeEach
    void setUp() {
        tipoDelito = TipoDelito.builder().id(1L).nombre("HOMICIDIO").subtipos(new ArrayList<>()).build();
    }

    @Test
    @DisplayName("agregarSubtipo() debe añadir el subtipo a la lista y asignarle el tipo padre")
    void debeAgregarSubtipo() {
        SubtipoDelito subtipo = new SubtipoDelito();

        tipoDelito.agregarSubtipo(subtipo);

        assertThat(tipoDelito.getSubtipos()).contains(subtipo);
        assertThat(subtipo.getTipoDelito()).isSameAs(tipoDelito);
    }

    @Test
    @DisplayName("obtenerSubtipos() debe retornar la lista actual de subtipos")
    void debeObtenerSubtipos() {
        SubtipoDelito subtipo = new SubtipoDelito();
        tipoDelito.agregarSubtipo(subtipo);

        assertThat(tipoDelito.obtenerSubtipos()).containsExactly(subtipo);
    }

    @Test
    @DisplayName("validarSubtipo() debe retornar true cuando el subtipo pertenece a la lista")
    void debeValidarSubtipoExistente() {
        SubtipoDelito subtipo = new SubtipoDelito();
        tipoDelito.agregarSubtipo(subtipo);

        assertThat(tipoDelito.validarSubtipo(subtipo)).isTrue();
    }

    @Test
    @DisplayName("validarSubtipo() debe retornar false cuando el subtipo no pertenece a la lista")
    void debeValidarSubtipoInexistente() {
        assertThat(tipoDelito.validarSubtipo(new SubtipoDelito())).isFalse();
    }

    @Test
    @DisplayName("esSubtipoObligatorio() debe reflejar el valor de requiereSubtipo")
    void debeIndicarSiSubtipoEsObligatorio() {
        tipoDelito.setRequiereSubtipo(true);
        assertThat(tipoDelito.esSubtipoObligatorio()).isTrue();

        tipoDelito.setRequiereSubtipo(false);
        assertThat(tipoDelito.esSubtipoObligatorio()).isFalse();

        tipoDelito.setRequiereSubtipo(null);
        assertThat(tipoDelito.esSubtipoObligatorio()).isFalse();
    }
}
