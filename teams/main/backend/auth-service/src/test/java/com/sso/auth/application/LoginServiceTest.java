package com.sso.auth.application;

import com.sso.auth.application.service.LoginService;
import com.sso.auth.domain.model.RefreshToken;
import com.sso.auth.domain.model.User;
import com.sso.auth.domain.port.in.LoginUseCase;
import com.sso.auth.domain.port.out.RefreshTokenRepository;
import com.sso.auth.domain.port.out.UserRepository;
import com.sso.auth.infrastructure.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del caso de uso LoginService.
 *
 * <p>Verifica la lógica de autenticación, la generación de tokens
 * y el manejo seguro de credenciales incorrectas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService — Casos de uso")
class LoginServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    private LoginService loginService;

    private final User testUser = new User(
            UUID.randomUUID(), "john_doe", "$2a$10$hashed", "John Doe", null, Instant.now());

    @BeforeEach
    void setUp() {
        loginService = new LoginService(userRepository, refreshTokenRepository,
                passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Debe retornar accessToken y refreshToken con credenciales válidas")
    void shouldReturnTokensOnValidCredentials() {
        // Arrange
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("RawPass", "$2a$10$hashed")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken("john_doe")).thenReturn("jwt_token");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        LoginUseCase.AuthResult result = loginService.login(
                new LoginUseCase.LoginCommand("john_doe", "RawPass"));

        // Assert
        assertThat(result.accessToken()).isEqualTo("jwt_token");
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.username()).isEqualTo("john_doe");
    }

    @Test
    @DisplayName("Debe lanzar InvalidCredentialsException si el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                loginService.login(new LoginUseCase.LoginCommand("ghost", "pass")))
                .isInstanceOf(LoginUseCase.InvalidCredentialsException.class)
                .hasMessageContaining("Credenciales inválidas");

        // No debe llamar al encoder (el usuario no existe)
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar InvalidCredentialsException si la contraseña es incorrecta")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPass", "$2a$10$hashed")).thenReturn(false);

        assertThatThrownBy(() ->
                loginService.login(new LoginUseCase.LoginCommand("john_doe", "WrongPass")))
                .isInstanceOf(LoginUseCase.InvalidCredentialsException.class);

        // No debe generar tokens si la contraseña falla
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("El mensaje de error debe ser genérico (no revelar si el usuario existe)")
    void shouldReturnGenericErrorMessage() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                loginService.login(new LoginUseCase.LoginCommand("ghost", "pass")))
                .hasMessage("Credenciales inválidas.");
    }
}
