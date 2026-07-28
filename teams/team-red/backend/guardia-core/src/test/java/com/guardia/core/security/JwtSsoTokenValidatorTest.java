package com.guardia.core.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtSsoTokenValidator - Pruebas Unitarias")
class JwtSsoTokenValidatorTest {

    private static final String SECRETO = "una-clave-secreta-de-al-menos-32-bytes-para-hmac-sha256";

    private JwtSsoTokenValidator validator;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        validator = new JwtSsoTokenValidator(SECRETO);
        signingKey = Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8));
    }

    private String tokenValido(String username, Duration validezDesdeAhora) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + validezDesdeAhora.toMillis()))
                .signWith(signingKey)
                .compact();
    }

    @Test
    @DisplayName("Debe extraer el username de un token válido y correctamente firmado")
    void debeExtraerUsernameDeTokenValido() {
        String token = tokenValido("jperez", Duration.ofMinutes(10));

        Optional<String> resultado = validator.validarYExtraerUsuario(token);

        assertThat(resultado).contains("jperez");
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el token es nulo o está en blanco")
    void debeRetornarVacioConTokenNuloOBlanco() {
        assertThat(validator.validarYExtraerUsuario(null)).isEmpty();
        assertThat(validator.validarYExtraerUsuario("   ")).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el token está mal formado")
    void debeRetornarVacioConTokenMalFormado() {
        assertThat(validator.validarYExtraerUsuario("esto-no-es-un-jwt-valido")).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el token fue firmado con una clave distinta")
    void debeRetornarVacioConFirmaInvalida() {
        SecretKey otraClave = Keys.hmacShaKeyFor("otra-clave-secreta-completamente-distinta-32b".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("jperez")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otraClave)
                .compact();

        assertThat(validator.validarYExtraerUsuario(token)).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el token ya expiró")
    void debeRetornarVacioConTokenExpirado() {
        Date pasado = new Date(System.currentTimeMillis() - Duration.ofMinutes(10).toMillis());
        String tokenExpirado = Jwts.builder()
                .subject("jperez")
                .issuedAt(new Date(pasado.getTime() - 60_000))
                .expiration(pasado)
                .signWith(signingKey)
                .compact();

        assertThat(validator.validarYExtraerUsuario(tokenExpirado)).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el token no tiene subject")
    void debeRetornarVacioSinSubject() {
        String token = Jwts.builder()
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(signingKey)
                .compact();

        assertThat(validator.validarYExtraerUsuario(token)).isEmpty();
    }
}
