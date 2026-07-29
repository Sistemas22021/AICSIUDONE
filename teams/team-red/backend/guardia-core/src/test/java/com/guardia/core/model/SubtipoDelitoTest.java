package com.guardia.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SubtipoDelito - Pruebas Unitarias de Modelo")
class SubtipoDelitoTest {

    @Test
    @DisplayName("validarCorrespondenciaConTipo() debe retornar true cuando el tipo padre coincide")
    void debeRetornarTrueCuandoCoincide() {
        TipoDelito tipo = TipoDelito.builder().id(1L).nombre("HOMICIDIO").build();
        SubtipoDelito subtipo = SubtipoDelito.builder().id(10L).tipoDelito(tipo).build();

        assertThat(subtipo.validarCorrespondenciaConTipo(tipo)).isTrue();
    }

    @Test
    @DisplayName("validarCorrespondenciaConTipo() debe retornar false cuando el tipo no coincide")
    void debeRetornarFalseCuandoNoCoincide() {
        TipoDelito tipoA = TipoDelito.builder().id(1L).nombre("HOMICIDIO").build();
        TipoDelito tipoB = TipoDelito.builder().id(2L).nombre("ROBO").build();
        SubtipoDelito subtipo = SubtipoDelito.builder().id(10L).tipoDelito(tipoA).build();

        assertThat(subtipo.validarCorrespondenciaConTipo(tipoB)).isFalse();
    }

    @Test
    @DisplayName("validarCorrespondenciaConTipo() debe retornar false cuando el subtipo no tiene tipo asignado")
    void debeRetornarFalseSinTipoAsignado() {
        SubtipoDelito subtipo = SubtipoDelito.builder().id(10L).build();
        assertThat(subtipo.validarCorrespondenciaConTipo(TipoDelito.builder().id(1L).build())).isFalse();
    }
}
