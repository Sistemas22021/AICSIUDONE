package com.nexocriminal.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

@Tag(name = "Autenticación", description = "Login, registro de usuarios y cambio de contraseña con JWT")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Operation(summary = "Iniciar sesión",
               description = "Valida las credenciales y devuelve un token JWT con el rol del usuario")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Usuario user = usuarioRepository.findByUsername(req.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        String rol = user.getRol() != null ? user.getRol() : "ANALISTA";
        String token = jwtService.generarToken(user.getUsername(), rol);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "nombreCompleto", user.getNombreCompleto() != null ? user.getNombreCompleto() : user.getUsername(),
                "rol", rol
        ));
    }

    @Operation(summary = "Registrar usuario",
               description = "Crea un usuario nuevo con rol ANALISTA por defecto")
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest req) {
        if (usuarioRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El usuario ya existe"));
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(req.getUsername());
        nuevo.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        nuevo.setNombreCompleto(req.getNombreCompleto() != null ? req.getNombreCompleto() : req.getUsername());
        nuevo.setRol("ANALISTA");
        usuarioRepository.save(nuevo);

        return ResponseEntity.ok(Map.of(
                "ok", "Usuario creado correctamente",
                "username", nuevo.getUsername()
        ));
    }

    @Operation(summary = "Cambiar contraseña",
               description = "Cambia la contraseña de un usuario validando la actual")
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String passwordActual = body.get("passwordActual");
        String passwordNueva = body.get("passwordNueva");

        if (username == null || passwordActual == null || passwordNueva == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Faltan parámetros"));
        }

        Usuario user = usuarioRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        if (!passwordEncoder.matches(passwordActual, user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "La contraseña actual es incorrecta"));
        }

        if (passwordNueva.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La nueva contraseña debe tener al menos 6 caracteres"));
        }

        user.setPasswordHash(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(user);

        return ResponseEntity.ok(Map.of("ok", "Contraseña actualizada correctamente"));
    }

    /**
     * DEMOSTRACION DE COMPATIBILIDAD CON EL SSO CENTRALIZADO.
     *
     * Genera un token JWT firmado con el mismo secreto y algoritmo (HS256) que
     * usa el Auth Service del login unico. Sirve para demostrar que este sistema
     * valida correctamente los tokens emitidos por el SSO: si un token firmado
     * con el secreto compartido es aceptado en los endpoints protegidos, entonces
     * un token real del Auth Service tambien lo sera.
     *
     * NOTA DE SEGURIDAD: el token se emite SIEMPRE con el rol ANALISTA. No se
     * permite elegir el rol desde la peticion, para que este endpoint no pueda
     * usarse para obtener privilegios de administrador sin credenciales.
     * En un despliegue productivo real este endpoint se retira.
     */
    @Operation(summary = "[DEMO SSO] Generar token estilo SSO",
               description = "Genera un JWT firmado con el secreto compartido (HS256), simulando el que "
                           + "emitiría el Auth Service del login centralizado. El rol es siempre ANALISTA. "
                           + "Existe únicamente para demostrar la compatibilidad SSO; se retira en producción.")
    @PostMapping("/sso-token-demo")
    public ResponseEntity<?> ssoTokenDemo(@RequestBody(required = false) Map<String, String> body) {
        String username = (body != null && body.get("username") != null && !body.get("username").isBlank())
                ? body.get("username")
                : "usuario.sso";

        // El rol es fijo: este endpoint no puede usarse para escalar privilegios.
        final String rol = "ANALISTA";

        // Se firma con el MISMO JwtService (mismo secreto, mismo HS256) que valida el resto del sistema.
        String token = jwtService.generarToken(username, rol);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", username,
                "rol", rol,
                "nota", "Token estilo SSO firmado con el secreto compartido (HS256). "
                      + "Usalo como 'Authorization: Bearer <token>'. El rol es siempre ANALISTA."
        ));
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Usuario requerido")
        private String username;

        @NotBlank(message = "Contraseña requerida")
        private String password;
    }

    @Data
    public static class RegistroRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        private String nombreCompleto;
    }
}