package com.guardia.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BCryptPasswordHasher - Pruebas Unitarias")
class BCryptPasswordHasherTest {

    private BCryptPasswordHasher hasher;

    @BeforeEach
    void setUp() {
        // Se usa un encoder real (no mock) para validar el comportamiento criptográfico genuino.
        hasher = new BCryptPasswordHasher(new BCryptPasswordEncoder());
    }

    @Nested
    @DisplayName("hash()")
    class Hash {

        @Test
        @DisplayName("Debe producir un hash BCrypt distinto del texto plano")
        void debeProducirHashDistintoDelTextoPlano() {
            String hash = hasher.hash("miClaveSegura123");

            assertThat(hash).isNotEqualTo("miClaveSegura123");
            assertThat(hash).startsWith("$2");
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando la contraseña es nula")
        void debeLanzarExcepcionConPasswordNulo() {
            assertThatThrownBy(() -> hasher.hash(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando la contraseña está en blanco")
        void debeLanzarExcepcionConPasswordEnBlanco() {
            assertThatThrownBy(() -> hasher.hash("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("matches()")
    class Matches {

        @Test
        @DisplayName("Debe retornar true cuando la contraseña en texto plano corresponde al hash")
        void debeRetornarTrueCuandoCoincide() {
            String hash = hasher.hash("claveCorrecta");
            assertThat(hasher.matches("claveCorrecta", hash)).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando la contraseña no corresponde al hash")
        void debeRetornarFalseCuandoNoCoincide() {
            String hash = hasher.hash("claveCorrecta");
            assertThat(hasher.matches("claveIncorrecta", hash)).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando la contraseña en texto plano es nula o está en blanco")
        void debeRetornarFalseConPasswordNuloOBlanco() {
            String hash = hasher.hash("claveCorrecta");
            assertThat(hasher.matches(null, hash)).isFalse();
            assertThat(hasher.matches("  ", hash)).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando el hash almacenado es nulo o está en blanco")
        void debeRetornarFalseConHashNuloOBlanco() {
            assertThat(hasher.matches("clave", null)).isFalse();
            assertThat(hasher.matches("clave", "  ")).isFalse();
        }
    }
}
