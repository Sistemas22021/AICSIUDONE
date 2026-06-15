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
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado exitosamente.", usuarioService.crear(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerPorId(id)));
    }

    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorIdentificacion(@PathVariable String identificacion) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerPorIdentificacion(identificacion)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerTodos()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(@PathVariable Long id,
                                                                    @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado.", usuarioService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado.", null));
    }

    @PostMapping("/{id}/autenticar")
    public ResponseEntity<ApiResponse<Boolean>> autenticar(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        boolean resultado = usuarioService.autenticar(id, body.get("credenciales"));
        return ResponseEntity.ok(ApiResponse.ok("Autenticación procesada.", resultado));
    }
}
