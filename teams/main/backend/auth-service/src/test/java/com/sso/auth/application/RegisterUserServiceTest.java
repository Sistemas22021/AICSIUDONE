package com.sso.auth.application;

import com.sso.auth.application.service.RegisterUserService;
import com.sso.auth.domain.model.User;
import com.sso.auth.domain.port.in.RegisterUserUseCase;
import com.sso.auth.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del caso de uso RegisterUserService.
 *
 * <p><b>TDD Cycle aplicado:</b>
 * <ol>
 *   <li>RED: Se escribió cada @Test antes de la implementación</li>
 *   <li>GREEN: Se implementó el mínimo código para pasar</li>
 *   <li>REFACTOR: Se mejoró la legibilidad del service sin romper tests</li>
 * </ol>
 *
 * <p><b>Estrategia de testing:</b>
 * Se usa Mockito puro, sin Spring Context. Al no arrancar Spring,
 * los tests son extremadamente rápidos (< 50ms cada uno).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserService — Casos de uso")
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        registerUserService = new RegisterUserService(userRepository, passwordEncoder);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Happy Path
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe registrar un usuario nuevo correctamente")
    void shouldRegisterNewUserSuccessfully() {
        // Arrange (Given)
        var command = new RegisterUserUseCase.RegisterCommand(
                "john_doe", "RawPass123!", "John Doe", null);

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(passwordEncoder.encode("RawPass123!")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act (When)
        User result = registerUserService.register(command);

        // Assert (Then)
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("john_doe");
        assertThat(result.fullName()).isEqualTo("John Doe");
        assertThat(result.password()).isEqualTo("$2a$10$hashedPassword");
        assertThat(result.id()).isNotNull();

        // Verificar que se llamó al encoder y al repositorio
        verify(passwordEncoder).encode("RawPass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Nunca debe guardar la contraseña en texto plano")
    void shouldNeverStoreRawPassword() {
        // Arrange
        var command = new RegisterUserUseCase.RegisterCommand(
                "secure_user", "RawPass123!", "Secure User", null);

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        User result = registerUserService.register(command);

        // Assert — la contraseña almacenada NO debe ser la original
        assertThat(result.password()).isNotEqualTo("RawPass123!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Casos de Error
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe lanzar excepción si el username ya existe")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange
        var command = new RegisterUserUseCase.RegisterCommand(
                "existing_user", "Pass123!", "Existing User", null);

        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> registerUserService.register(command))
                .isInstanceOf(RegisterUserUseCase.UsernameAlreadyExistsException.class)
                .hasMessageContaining("existing_user");

        // El repositorio NO debe llamar a save si el usuario ya existe
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
