package com.sso.auth.domain.port.in;

/**
 * Puerto de entrada (Use Case): Login.
 *
 * <p>Autentica las credenciales del usuario y retorna los tokens.
 * El resultado incluye tanto el accessToken (JWT corto plazo)
 * como el refreshToken (opaco, largo plazo).
 */
public interface LoginUseCase {

    /**
     * Autentica al usuario y genera los tokens de sesión.
     *
     * @param command Credenciales del usuario
     * @return Tokens de acceso y refresco
     * @throws InvalidCredentialsException si username o password son incorrectos
     */
    AuthResult login(LoginCommand command);

    /** Credenciales de entrada */
    record LoginCommand(String username, String rawPassword) {}

    /**
     * Resultado de la autenticación exitosa.
     *
     * <p>{@code accessToken}: JWT firmado, expira en 15 minutos.
     * El cliente lo guarda en memoria (variable JS), nunca en localStorage.
     *
     * <p>{@code refreshToken}: UUID opaco, expira en 7 días.
     * Se entrega al cliente como HttpOnly Cookie, nunca en el body.
     */
    record AuthResult(String accessToken, String refreshToken, String username) {}

    /** Excepción de dominio: credenciales incorrectas */
    class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            // Mensaje genérico intencional: no revelar si el usuario existe
            super("Credenciales inválidas.");
        }
    }
}
