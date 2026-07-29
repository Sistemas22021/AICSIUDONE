package com.guardia.core.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityBeansConfig - Pruebas Unitarias")
class SecurityBeansConfigTest {

    @Test
    @DisplayName("passwordEncoder() debe exponer un BCryptPasswordEncoder funcional")
    void debeExponerBCryptPasswordEncoder() {
        SecurityBeansConfig config = new SecurityBeansConfig();

        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
        String hash = encoder.encode("clave123");
        assertThat(encoder.matches("clave123", hash)).isTrue();
    }
}
