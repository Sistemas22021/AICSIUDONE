package com.guardia.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.GlobalExceptionHandler;
import com.guardia.core.middleware.AuthenticationHandler;
import com.guardia.core.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController - Pruebas Unitarias")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UsuarioResponse responseEjemplo(UUID id) {
        return new UsuarioResponse(id, "jperez", "Juan Perez", null, "OFICIAL");
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{id} debe retornar el usuario solicitado")
    void debeObtenerPorId() throws Exception {
        UUID id = UUID.randomUUID();
        when(usuarioService.obtenerPorId(id)).thenReturn(responseEjemplo(id));

        mockMvc.perform(get("/api/v1/usuarios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("jperez"));
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/username/{username} debe resolver directamente el username indicado")
    void debeObtenerPorUsernameDirecto() throws Exception {
        when(usuarioService.obtenerPorUsername("jperez")).thenReturn(responseEjemplo(UUID.randomUUID()));

        mockMvc.perform(get("/api/v1/usuarios/username/jperez"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/username/me debe resolver el username autenticado del request")
    void debeResolverMeAlUsuarioAutenticado() throws Exception {
        when(usuarioService.obtenerPorUsername("autenticado")).thenReturn(responseEjemplo(UUID.randomUUID()));

        mockMvc.perform(get("/api/v1/usuarios/username/me")
                        .requestAttr(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME, "autenticado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("jperez"));
    }

    @Test
    @DisplayName("GET /api/v1/usuarios debe retornar todos los usuarios")
    void debeObtenerTodos() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(List.of(responseEjemplo(UUID.randomUUID())));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/usuarios/{id} debe actualizar el usuario")
    void debeActualizar() throws Exception {
        UUID id = UUID.randomUUID();
        UsuarioRequest request = new UsuarioRequest("jperez", "claveSegura1", "Juan Perez Actualizado", null);
        when(usuarioService.actualizar(eq(id), any(UsuarioRequest.class))).thenReturn(responseEjemplo(id));

        mockMvc.perform(put("/api/v1/usuarios/" + id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario actualizado."));
    }

    @Test
    @DisplayName("PUT /api/v1/usuarios/{id} debe responder 400 cuando la contraseña es muy corta")
    void debeRechazarPasswordCorta() throws Exception {
        UUID id = UUID.randomUUID();
        UsuarioRequest request = new UsuarioRequest("jperez", "123", "Juan Perez", null);

        mockMvc.perform(put("/api/v1/usuarios/" + id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{id} debe eliminar el usuario")
    void debeEliminar() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/usuarios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado."));
    }
}
