package com.guardia.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EscenaNegativa - Pruebas Unitarias de Modelo")
class EscenaNegativaTest {

    @Test
    @DisplayName("registrarElementoBuscado() debe asignar el elemento buscado")
    void debeRegistrarElementoBuscado() {
        EscenaNegativa en = new EscenaNegativa();
        en.registrarElementoBuscado("Arma de fuego");
        assertThat(en.getElementoBuscado()).isEqualTo("Arma de fuego");
    }

    @Test
    @DisplayName("registrarResultadoNoEncontrado() debe asignar área, observación y marcar resultado NO_ENCONTRADO")
    void debeRegistrarResultadoNoEncontrado() {
        EscenaNegativa en = new EscenaNegativa();
        en.registrarResultadoNoEncontrado("Habitación principal", "Se revisó cuidadosamente");

        assertThat(en.getAreaInspeccionada()).isEqualTo("Habitación principal");
        assertThat(en.getObservacion()).isEqualTo("Se revisó cuidadosamente");
        assertThat(en.getResultado()).isEqualTo("NO_ENCONTRADO");
    }

    @Nested
    @DisplayName("agregarObservacion()")
    class AgregarObservacion {

        @Test
        @DisplayName("Debe asignar la observación directamente cuando no había ninguna previa")
        void debeAsignarObservacionInicial() {
            EscenaNegativa en = new EscenaNegativa();
            en.agregarObservacion("Primera observación");
            assertThat(en.getObservacion()).isEqualTo("Primera observación");
        }

        @Test
        @DisplayName("Debe concatenar con separador cuando ya existía una observación previa")
        void debeConcatenarObservaciones() {
            EscenaNegativa en = new EscenaNegativa();
            en.setObservacion("Primera");
            en.agregarObservacion("Segunda");
            assertThat(en.getObservacion()).isEqualTo("Primera | Segunda");
        }
    }

    @Nested
    @DisplayName("validarRegistro()")
    class ValidarRegistro {

        @Test
        @DisplayName("Debe retornar true cuando elemento y área están informados")
        void debeRetornarTrueConDatosCompletos() {
            EscenaNegativa en = EscenaNegativa.builder()
                    .elementoBuscado("Arma").areaInspeccionada("Cocina").build();
            assertThat(en.validarRegistro()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el elemento buscado")
        void debeRetornarFalseSinElemento() {
            EscenaNegativa en = EscenaNegativa.builder().areaInspeccionada("Cocina").build();
            assertThat(en.validarRegistro()).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el área inspeccionada")
        void debeRetornarFalseSinArea() {
            EscenaNegativa en = EscenaNegativa.builder().elementoBuscado("Arma").build();
            assertThat(en.validarRegistro()).isFalse();
        }
    }

    @Test
    @DisplayName("marcarSinElementosNegativos() debe completar los campos con los valores estándar")
    void debeMarcarSinElementosNegativos() {
        EscenaNegativa en = new EscenaNegativa();
        en.marcarSinElementosNegativos();

        assertThat(en.getSinElementosNegativos()).isTrue();
        assertThat(en.getElementoBuscado()).isEqualTo("SIN_ELEMENTOS_NEGATIVOS");
        assertThat(en.getAreaInspeccionada()).isEqualTo("N/A");
        assertThat(en.getResultado()).isEqualTo("SIN_HALLAZGOS");
        assertThat(en.getObservacion()).contains("no hay elementos negativos");
    }
}
