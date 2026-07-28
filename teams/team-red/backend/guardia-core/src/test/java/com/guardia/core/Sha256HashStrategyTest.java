package com.guardia.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sha256HashStrategy - Pruebas Unitarias")
class Sha256HashStrategyTest {

    private final Sha256HashStrategy strategy = new Sha256HashStrategy();

    @Test
    @DisplayName("Debe calcular un hash SHA-256 hexadecimal de 64 caracteres")
    void debeCalcularHashDeLongitudCorrecta() {
        String hash = strategy.calcular("contenido de prueba");

        assertThat(hash).hasSize(64);
        assertThat(hash).matches("[0-9a-f]{64}");
    }

    @Test
    @DisplayName("Debe ser determinista: el mismo contenido siempre produce el mismo hash")
    void debeSerDeterminista() {
        String hash1 = strategy.calcular("mismo contenido");
        String hash2 = strategy.calcular("mismo contenido");

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Debe producir hashes distintos para contenidos distintos")
    void debeProducirHashesDistintosParaContenidosDistintos() {
        String hash1 = strategy.calcular("contenido A");
        String hash2 = strategy.calcular("contenido B");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Debe calcular correctamente el hash conocido de una cadena vacía")
    void debeCalcularHashDeCadenaVacia() {
        // SHA-256("") es un valor bien conocido y estable
        assertThat(strategy.calcular(""))
                .isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }
}
