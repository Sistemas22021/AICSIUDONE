package com.guardia.core.model;

import com.guardia.core.model.enums.EstadoEscena;
import com.guardia.core.model.enums.PasoChecklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Escena - Pruebas Unitarias de Modelo")
class EscenaTest {

    private Escena escena;

    @BeforeEach
    void setUp() {
        escena = Escena.builder()
                .id(1L)
                .escenasNegativas(new ArrayList<>())
                .checklist(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("iniciarChecklist() debe marcar el estado INICIADO y registrar el inicio del proceso")
    void debeIniciarChecklist() {
        escena.iniciarChecklist();

        assertThat(escena.getEstadoChecklist()).isEqualTo("INICIADO");
        assertThat(escena.getInicioProceso()).isNotNull();
    }

    @Test
    @DisplayName("completarPaso() debe añadir la escena negativa y vincularla a la escena")
    void debeCompletarPaso() {
        EscenaNegativa negativa = new EscenaNegativa();

        escena.completarPaso(negativa);

        assertThat(escena.getEscenasNegativas()).contains(negativa);
        assertThat(negativa.getEscena()).isSameAs(escena);
    }

    @Test
    @DisplayName("completarPaso() no debe fallar cuando el item es null")
    void debeIgnorarPasoNulo() {
        escena.completarPaso(null);
        assertThat(escena.getEscenasNegativas()).isEmpty();
    }

    @Nested
    @DisplayName("registrarTimestampPaso()")
    class RegistrarTimestampPaso {

        @Test
        @DisplayName("Debe registrar fechaCierre cuando esCierre es true")
        void debeRegistrarFechaCierre() {
            EscenaChecklist paso = EscenaChecklist.builder().build();
            escena.registrarTimestampPaso(paso, true);
            assertThat(paso.getFechaCierre()).isNotNull();
            assertThat(paso.getFechaInicio()).isNull();
        }

        @Test
        @DisplayName("Debe registrar fechaInicio cuando esCierre es false")
        void debeRegistrarFechaInicio() {
            EscenaChecklist paso = EscenaChecklist.builder().build();
            escena.registrarTimestampPaso(paso, false);
            assertThat(paso.getFechaInicio()).isNotNull();
            assertThat(paso.getFechaCierre()).isNull();
        }

        @Test
        @DisplayName("No debe fallar cuando el paso es null")
        void noDebeFallarConPasoNulo() {
            assertThat(escena).satisfies(e -> e.registrarTimestampPaso(null, true));
        }
    }

    @Nested
    @DisplayName("validarSecuencia()")
    class ValidarSecuencia {

        @Test
        @DisplayName("Debe retornar false cuando el checklist está vacío")
        void debeRetornarFalseConChecklistVacio() {
            assertThat(escena.validarSecuencia()).isFalse();
        }

        @Test
        @DisplayName("Debe retornar true cuando los pasos completados están en orden ascendente")
        void debeRetornarTrueConSecuenciaValida() {
            escena.setChecklist(List.of(
                    paso(1, PasoChecklist.ASEGURAMIENTO_PERIMETRO, true),
                    paso(2, PasoChecklist.DOCUMENTACION_EVIDENCIA, true),
                    paso(3, PasoChecklist.RECOLECCION_EMBALAJE, false)
            ));

            assertThat(escena.validarSecuencia()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando un paso está completado sin que el anterior lo esté")
        void debeRetornarFalseConSecuenciaInvalida() {
            escena.setChecklist(List.of(
                    paso(1, PasoChecklist.ASEGURAMIENTO_PERIMETRO, false),
                    paso(2, PasoChecklist.DOCUMENTACION_EVIDENCIA, true)
            ));

            assertThat(escena.validarSecuencia()).isFalse();
        }

        private EscenaChecklist paso(int orden, PasoChecklist tipo, boolean completado) {
            return EscenaChecklist.builder().orden(orden).paso(tipo).completado(completado).build();
        }
    }

    @Test
    @DisplayName("cerrar() debe marcar el estado CERRADO y registrar el cierre del proceso")
    void debeCerrar() {
        escena.cerrar();
        assertThat(escena.getEstadoChecklist()).isEqualTo("CERRADO");
        assertThat(escena.getCierreProceso()).isNotNull();
    }

    @Test
    @DisplayName("bloquearEdicion() debe marcar el estado BLOQUEADO")
    void debeBloquearEdicion() {
        escena.bloquearEdicion();
        assertThat(escena.getEstadoChecklist()).isEqualTo("BLOQUEADO");
    }

    @Test
    @DisplayName("liberar() debe completar todos los campos de liberación y marcar el estado LIBERADA")
    void debeLiberarEscena() {
        Usuario investigador = Usuario.builder().username("inv").fullName("Investigador").build();
        LocalDateTime horaCierre = LocalDateTime.of(2026, 1, 20, 10, 0);

        escena.liberar(investigador, horaCierre, "Todo en orden", "hash-abc");

        assertThat(escena.getLiberadaPor()).isSameAs(investigador);
        assertThat(escena.getHoraLiberacion()).isEqualTo(horaCierre);
        assertThat(escena.getObservacionesLiberacion()).isEqualTo("Todo en orden");
        assertThat(escena.getHashLiberacion()).isEqualTo("hash-abc");
        assertThat(escena.getEstado()).isEqualTo(EstadoEscena.LIBERADA);
        assertThat(escena.getEstadoChecklist()).isEqualTo("COMPLETADO");
        assertThat(escena.getPasoActual()).isNull();
        assertThat(escena.getCierreProceso()).isEqualTo(horaCierre);
    }

    @Nested
    @DisplayName("estaLiberada()")
    class EstaLiberada {

        @Test
        @DisplayName("Debe retornar true cuando el estado es LIBERADA")
        void debeRetornarTrueCuandoLiberada() {
            escena.setEstado(EstadoEscena.LIBERADA);
            assertThat(escena.estaLiberada()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando el estado es ACTIVA")
        void debeRetornarFalseCuandoActiva() {
            escena.setEstado(EstadoEscena.ACTIVA);
            assertThat(escena.estaLiberada()).isFalse();
        }
    }
}
