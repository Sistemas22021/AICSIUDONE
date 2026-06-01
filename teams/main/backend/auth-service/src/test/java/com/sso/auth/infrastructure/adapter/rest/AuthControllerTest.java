package com.sso.auth.infrastructure.adapter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sso.auth.domain.model.User;
import com.sso.auth.domain.port.in.LoginUseCase;
import com.sso.auth.domain.port.in.RefreshTokenUseCase;
import com.sso.auth.domain.port.in.RegisterUserUseCase;
import com.sso.auth.infrastructure.adapter.in.rest.AuthController;
import com.sso.auth.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.sso.auth.infrastructure.adapter.in.rest.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests del Controller REST usando {@code @WebMvcTest}.
 *
 * <p>Carga solo el slice web (filtros, controladores, serializadores).
 * Los puertos de dominio se mockean con {@code @MockBean}.
 *
 * <p>Verifica:
 * <ul>
 *   <li>Serialización/deserialización correcta (JSON ↔ DTO)</li>
 *   <li>Validaciones (@Valid, @NotBlank)</li>
 *   <li>Códigos HTTP correctos</li>
 *   <li>Que el refreshToken va en Cookie, no en el body</li>
 * </ul>
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController — Tests REST")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private RegisterUserUseCase registerUserUseCase;
    @MockBean private LoginUseCase loginUseCase;
    @MockBean private RefreshTokenUseCase refreshTokenUseCase;

    @Test
    @DisplayName("POST /register debe retornar 201 con datos del usuario")
    @WithMockUser
    void shouldReturn201OnSuccessfulRegister() throws Exception {
        var createdUser = new User(UUID.randomUUID(), "john_doe",
                "hashed", "John Doe", null, Instant.now());
        when(registerUserUseCase.register(any())).thenReturn(createdUser);

        var request = new RegisterRequest("john_doe", "SecurePass123!", "John Doe", null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @DisplayName("POST /login debe retornar accessToken en body y NO el refreshToken")
    @WithMockUser
    void shouldReturnAccessTokenInBodyAndRefreshTokenInCookie() throws Exception {
        when(loginUseCase.login(any())).thenReturn(
                new LoginUseCase.AuthResult("jwt_access_token", "refresh_uuid", "john_doe"));

        var request = new LoginRequest("john_doe", "SecurePass123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt_access_token"))
                // El refreshToken NO debe aparecer en el body
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                // El refreshToken SÍ debe estar en el Cookie
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token", true));
    }

    @Test
    @DisplayName("POST /register debe retornar 400 si falta username")
    @WithMockUser
    void shouldReturn400WhenUsernameIsMissing() throws Exception {
        var invalidRequest = new RegisterRequest("", "Pass123!", "Name", null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register debe retornar 409 si el username ya existe")
    @WithMockUser
    void shouldReturn409WhenUsernameAlreadyExists() throws Exception {
        when(registerUserUseCase.register(any()))
                .thenThrow(new RegisterUserUseCase.UsernameAlreadyExistsException("john_doe"));

        var request = new RegisterRequest("john_doe", "Pass123!", "John", null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
