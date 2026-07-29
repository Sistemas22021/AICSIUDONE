package com.guardia.core.model;

import com.guardia.core.model.enums.EstadoExpediente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Expediente - Pruebas Unitarias de Modelo")
class ExpedienteTest {

    private Expediente expediente;

    @BeforeEach
    void setUp() {
        expediente = Expediente.builder()
                .id(1L)
                .escenas(new ArrayList<>())
                .involucrados(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("validarDatos()")
    class ValidarDatos {

        @Test
        @DisplayName("Debe retornar true cuando hay descripción, tipo de delito y localización")
        void debeRetornarTrueConLocalizacion() {
            expediente.setDescripcionHecho("Descripción del hecho");
            expediente.setTipoDelito(TipoDelito.builder().id(1L).build());
            expediente.setLocalizacion(Localizacion.builder().id(1L).build());

            assertThat(expediente.validarDatos()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true cuando hay descripción, tipo de delito y municipio (sin localización)")
        void debeRetornarTrueConMunicipio() {
            expediente.setDescripcionHecho("Descripción del hecho");
            expediente.setTipoDelito(TipoDelito.builder().id(1L).build());
            expediente.setMunicipio("Libertador");

            assertThat(expediente.validarDatos()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta la descripción del hecho")
        void debeRetornarFalseSinDescripcion() {
            expediente.setTipoDelito(TipoDelito.builder().id(1L).build());
            expediente.setMunicipio("Libertador");

            assertThat(expediente.validarDatos()).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el tipo de delito")
        void debeRetornarFalseSinTipoDelito() {
            expediente.setDescripcionHecho("Descripción");
            expediente.setMunicipio("Libertador");

            assertThat(expediente.validarDatos()).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando no hay localización ni municipio")
        void debeRetornarFalseSinLocalizacionNiMunicipio() {
            expediente.setDescripcionHecho("Descripción");
            expediente.setTipoDelito(TipoDelito.builder().id(1L).build());

            assertThat(expediente.validarDatos()).isFalse();
        }
    }

    @Test
    @DisplayName("cambiarEstado() debe actualizar el estado del expediente")
    void debeCambiarEstado() {
        expediente.cambiarEstado(EstadoExpediente.EN_REVISION);
        assertThat(expediente.getEstadoExpediente()).isEqualTo(EstadoExpediente.EN_REVISION);
    }

    @Test
    @DisplayName("vincularEscena() debe añadir la escena y asignarle el expediente")
    void debeVincularEscena() {
        Escena escena = new Escena();

        expediente.vincularEscena(escena);

        assertThat(expediente.getEscenas()).contains(escena);
        assertThat(escena.getExpediente()).isSameAs(expediente);
    }

    @Test
    @DisplayName("vincularEscena() no debe fallar cuando la escena es null")
    void noDebeFallarConEscenaNula() {
        expediente.vincularEscena(null);
        assertThat(expediente.getEscenas()).isEmpty();
    }

    @Test
    @DisplayName("sellarUsuario() debe asignar el agente, la fecha de sellado y cambiar el estado")
    void debeSellarUsuario() {
        Usuario agente = Usuario.builder().username("agente").fullName("Agente").build();

        expediente.sellarUsuario(agente);

        assertThat(expediente.getSelladoPor()).isSameAs(agente);
        assertThat(expediente.getFechaSellado()).isNotNull();
        assertThat(expediente.getEstadoExpediente()).isEqualTo(EstadoExpediente.PROCESADO_Y_SELLADO);
    }

    @Test
    @DisplayName("agregarInvolucrado() debe añadir el involucrado y asignarle el expediente")
    void debeAgregarInvolucrado() {
        Involucrado involucrado = new Involucrado();

        expediente.agregarInvolucrado(involucrado);

        assertThat(expediente.getInvolucrados()).contains(involucrado);
        assertThat(involucrado.getExpediente()).isSameAs(expediente);
    }

    @Test
    @DisplayName("asignarFechaHecho() debe establecer la fecha del hecho")
    void debeAsignarFechaHecho() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 15, 20, 0);
        expediente.asignarFechaHecho(fecha);
        assertThat(expediente.getFechaHecho()).isEqualTo(fecha);
    }
}
