package com.guardia.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Usuario - Pruebas Unitarias de Modelo")
class UsuarioTest {

    @Test
    @DisplayName("verificarAcceso() debe retornar true cuando el permiso no es nulo ni está en blanco")
    void debeRetornarTrueConPermisoValido() {
        Usuario usuario = Usuario.builder().id(UUID.randomUUID()).username("u").build();
        assertThat(usuario.verificarAcceso("ESCRIBIR_EXPEDIENTE")).isTrue();
    }

    @Test
    @DisplayName("verificarAcceso() debe retornar false cuando el permiso es nulo o está en blanco")
    void debeRetornarFalseConPermisoInvalido() {
        Usuario usuario = Usuario.builder().id(UUID.randomUUID()).username("u").build();
        assertThat(usuario.verificarAcceso(null)).isFalse();
        assertThat(usuario.verificarAcceso("   ")).isFalse();
    }

    @Test
    @DisplayName("crearExpediente() debe retornar un expediente con creadoPor asignado a sí mismo")
    void debeCrearExpedienteConCreadoPorAsignado() {
        Usuario usuario = Usuario.builder().id(UUID.randomUUID()).username("u").build();

        Expediente expediente = usuario.crearExpediente();

        assertThat(expediente.getCreadoPor()).isSameAs(usuario);
    }

    @Test
    @DisplayName("consultarExpediente() debe retornar el expediente cuyo folio coincide")
    void debeConsultarExpedientePorFolio() {
        Expediente exp1 = Expediente.builder().folio("EXP-A").build();
        Expediente exp2 = Expediente.builder().folio("EXP-B").build();
        Usuario usuario = Usuario.builder().id(UUID.randomUUID()).username("u")
                .expedientesCreados(List.of(exp1, exp2)).build();

        assertThat(usuario.consultarExpediente("EXP-B")).isSameAs(exp2);
    }

    @Test
    @DisplayName("consultarExpediente() debe retornar null cuando ningún folio coincide")
    void debeRetornarNullCuandoFolioNoCoincide() {
        Usuario usuario = Usuario.builder().id(UUID.randomUUID()).username("u")
                .expedientesCreados(List.of(Expediente.builder().folio("EXP-A").build())).build();

        assertThat(usuario.consultarExpediente("INEXISTENTE")).isNull();
    }
}
