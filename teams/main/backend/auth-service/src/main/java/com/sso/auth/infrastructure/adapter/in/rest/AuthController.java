package com.sso.auth.infrastructure.adapter.in.rest;

import com.sso.auth.domain.port.in.LoginUseCase;
import com.sso.auth.domain.port.in.RefreshTokenUseCase;
import com.sso.auth.domain.port.in.RegisterUserUseCase;
import com.sso.auth.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.sso.auth.infrastructure.adapter.in.rest.dto.LoginResponse;
import com.sso.auth.infrastructure.adapter.in.rest.dto.RegisterRequest;
import com.sso.auth.infrastructure.adapter.in.rest.dto.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Adaptador de entrada REST: Endpoints de autenticación.
 *
 * <p>Esta clase es un adaptador en la arquitectura hexagonal. Solo transforma
 * peticiones HTTP en llamadas a los puertos de entrada (casos de uso).
 * No contiene lógica de negocio.
 *
 * <p><b>Responsabilidades del Controller:</b>
 * <ul>
 *   <li>Validar el formato de la petición (con @Valid)</li>
 *   <li>Traducir entre DTOs y objetos de dominio</li>
 *   <li>Gestionar el HttpOnly Cookie del refresh token</li>
 *   <li>Mapear excepciones de dominio a códigos HTTP (via @ExceptionHandler)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, login y refresco de tokens")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final int COOKIE_MAX_AGE_SECONDS = 7 * 24 * 60 * 60; // 7 días

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/auth/register
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea un usuario en la base de datos. La contraseña se hashea con BCrypt.")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        var command = new RegisterUserUseCase.RegisterCommand(
                request.username(),
                request.password(),
                request.fullName(),
                request.profilePhotoUrl()
        );

        var user = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(user.id().toString(), user.username(), user.fullName()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/auth/login
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
               description = """
                   Autentica las credenciales y retorna:
                   - **accessToken** (en el body): JWT de 15 min → guardar en memoria JS
                   - **refresh_token** (como HttpOnly Cookie): opaco de 7 días → automático
                   """)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        var command = new LoginUseCase.LoginCommand(request.username(), request.password());
        var result = loginUseCase.login(command);

        // El refresh token va en un HttpOnly Cookie, nunca en el body
        setRefreshTokenCookie(response, result.refreshToken());

        return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.username()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/auth/refresh
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar Access Token",
               description = "Lee el HttpOnly Cookie 'refresh_token' y emite un nuevo accessToken JWT.")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request,
                                                  HttpServletResponse response) {
        // Extraer el refresh token del Cookie (nunca del body)
        String refreshToken = extractRefreshTokenFromCookie(request);

        String newAccessToken = refreshTokenUseCase.refresh(refreshToken);

        // El username se puede extraer del token o del resultado — simplificado aquí
        return ResponseEntity.ok(new LoginResponse(newAccessToken, null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Exception Handlers — mapean excepciones de dominio a HTTP
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(RegisterUserUseCase.UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameExists(RegisterUserUseCase.UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(LoginUseCase.InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(LoginUseCase.InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenUseCase.InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshToken(RefreshTokenUseCase.InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos privados de utilidad
    // ─────────────────────────────────────────────────────────────────────────

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        cookie.setHttpOnly(true);   // Inaccesible desde JavaScript → protege contra XSS
        cookie.setSecure(true);     // Solo se envía por HTTPS (deshabilitado en dev con HTTP)
        cookie.setPath("/api/v1/auth/refresh"); // Solo se envía al endpoint de refresh
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RefreshTokenUseCase.InvalidRefreshTokenException();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(RefreshTokenUseCase.InvalidRefreshTokenException::new);
    }
}
