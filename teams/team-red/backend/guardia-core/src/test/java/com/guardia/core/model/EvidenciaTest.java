package com.guardia.core.model;

import com.guardia.core.HashStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Evidencia - Pruebas Unitarias de Modelo")
class EvidenciaTest {

    private final HashStrategy hashStrategy = contenido -> "HASH(" + contenido + ")";
    private Escena escena;
    private Usuario investigador;

    @BeforeEach
    void setUp() {
        escena = Escena.builder().id(1L).build();
        investigador = Usuario.builder().username("inv").fullName("Investigador").build();
    }

    @Nested
    @DisplayName("registrarEvidencia()")
    class RegistrarEvidencia {

        @Test
        @DisplayName("Debe calcular el hash con la estrategia cuando no se provee hash de cliente")
        void debeCalcularHashConEstrategia() {
            Evidencia evidencia = new Evidencia();

            Evidencia resultado = evidencia.registrarEvidencia(
                    escena, "ARMA", "Cuchillo", investigador, hashStrategy, null);

            assertThat(resultado).isSameAs(evidencia);
            assertThat(evidencia.getHashIntegridad()).isEqualTo("HASH(ARMA|Cuchillo)");
            assertThat(evidencia.getEscena()).isSameAs(escena);
            assertThat(evidencia.getInvestigador()).isSameAs(investigador);
            assertThat(evidencia.getTimestampRegistro()).isNotNull();
        }

        @Test
        @DisplayName("Debe usar el hash del cliente cuando viene informado, sin invocar la estrategia")
        void debeUsarHashDeCliente() {
            Evidencia evidencia = new Evidencia();

            evidencia.registrarEvidencia(escena, "ARMA", "Cuchillo", investigador, hashStrategy, "hash-cliente");

            assertThat(evidencia.getHashIntegridad()).isEqualTo("hash-cliente");
        }

        @Test
        @DisplayName("Debe recalcular con la estrategia cuando el hash de cliente viene en blanco")
        void debeRecalcularCuandoHashClienteEnBlanco() {
            Evidencia evidencia = new Evidencia();

            evidencia.registrarEvidencia(escena, "ARMA", "Cuchillo", investigador, hashStrategy, "   ");

            assertThat(evidencia.getHashIntegridad()).isEqualTo("HASH(ARMA|Cuchillo)");
        }
    }

    @Nested
    @DisplayName("verificarHash()")
    class VerificarHash {

        @Test
        @DisplayName("Debe retornar true cuando el hash recalculado coincide")
        void debeRetornarTrueCuandoCoincide() {
            Evidencia evidencia = Evidencia.builder().tipo("ARMA").descripcion("Cuchillo")
                    .hashIntegridad("HASH(ARMA|Cuchillo)").build();

            assertThat(evidencia.verificarHash(hashStrategy)).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando el hash recalculado no coincide")
        void debeRetornarFalseCuandoNoCoincide() {
            Evidencia evidencia = Evidencia.builder().tipo("ARMA").descripcion("Cuchillo")
                    .hashIntegridad("hash-alterado").build();

            assertThat(evidencia.verificarHash(hashStrategy)).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando no hay hash almacenado")
        void debeRetornarFalseSinHashAlmacenado() {
            Evidencia evidencia = Evidencia.builder().tipo("ARMA").descripcion("Cuchillo").build();
            assertThat(evidencia.verificarHash(hashStrategy)).isFalse();
        }
    }

    @Test
    @DisplayName("asignarNumero() debe establecer el número de item")
    void debeAsignarNumero() {
        Evidencia evidencia = new Evidencia();
        evidencia.asignarNumero("EV-007");
        assertThat(evidencia.getNumeroItem()).isEqualTo("EV-007");
    }

    @Nested
    @DisplayName("validarIntegridad()")
    class ValidarIntegridad {

        @Test
        @DisplayName("Debe retornar true cuando numeroItem, tipo y escena están presentes")
        void debeRetornarTrueConDatosCompletos() {
            Evidencia evidencia = Evidencia.builder().numeroItem("EV-001").tipo("ARMA").escena(escena).build();
            assertThat(evidencia.validarIntegridad()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta el número de item")
        void debeRetornarFalseSinNumeroItem() {
            Evidencia evidencia = Evidencia.builder().tipo("ARMA").escena(escena).build();
            assertThat(evidencia.validarIntegridad()).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando falta la escena")
        void debeRetornarFalseSinEscena() {
            Evidencia evidencia = Evidencia.builder().numeroItem("EV-001").tipo("ARMA").build();
            assertThat(evidencia.validarIntegridad()).isFalse();
        }
    }

    @Test
    @DisplayName("vincularEscena() debe asignar la escena a la evidencia")
    void debeVincularEscena() {
        Evidencia evidencia = new Evidencia();
        Escena otra = Escena.builder().id(2L).build();

        evidencia.vincularEscena(otra);

        assertThat(evidencia.getEscena()).isSameAs(otra);
    }

    @Test
    @DisplayName("firmarLevantamiento() no debe lanzar excepciones (lógica de auditoría pendiente)")
    void debeFirmarLevantamientoSinExcepcion() {
        Evidencia evidencia = new Evidencia();
        assertThat(evidencia).satisfies(e -> e.firmarLevantamiento(investigador));
    }
}
