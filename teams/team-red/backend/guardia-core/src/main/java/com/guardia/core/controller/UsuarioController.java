package com.guardia.core.controller;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
/**
 * Controlador REST para gestionar usuarios del sistema (investigadores, agentes).
 * Soporta creación, consulta, actualización, eliminación y autenticación básica.
 * Rutas principales: /api/v1/usuarios
 */
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerPorId(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorUsername(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerPorUsername(username)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerTodos()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable UUID id,  // Long → UUID
            @Valid @RequestBody UsuarioRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Usuario actualizado.",
                usuarioService.actualizar(id, request)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable UUID id  // Long → UUID
    ) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado.", null));
    }
}
