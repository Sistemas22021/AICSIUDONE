package com.guardia.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModusOperandi - Pruebas Unitarias de Modelo")
class ModusOperandiTest {

    @Test
    @DisplayName("analizarPatrones() debe asignar la lista de expedientes recibida")
    void debeAnalizarPatrones() {
        ModusOperandi mo = new ModusOperandi();
        List<Expediente> expedientes = List.of(new Expediente(), new Expediente());

        mo.analizarPatrones(expedientes);

        assertThat(mo.getExpedientes()).hasSize(2);
    }

    @Test
    @DisplayName("compararExpedientes() retorna 0.0 (lógica de comparación pendiente en el modelo)")
    void debeCompararExpedientes() {
        ModusOperandi mo = new ModusOperandi();
        assertThat(mo.compararExpedientes(new Expediente(), new Expediente())).isEqualTo(0.0);
    }

    @Test
    @DisplayName("agregarPatron() debe asignar el patrón detectado")
    void debeAgregarPatron() {
        ModusOperandi mo = new ModusOperandi();
        mo.agregarPatron("ROBO_NOCTURNO");
        assertThat(mo.getPatronDetectado()).isEqualTo("ROBO_NOCTURNO");
    }

    @Test
    @DisplayName("calcularNivelConfianza() y generarAlerta() no deben lanzar excepciones (lógica pendiente)")
    void debeEjecutarMetodosPendientesSinExcepcion() {
        ModusOperandi mo = new ModusOperandi();
        assertThat(mo).satisfies(m -> {
            m.calcularNivelConfianza();
            m.generarAlerta("criterio");
        });
    }
}
