package com.sso.auth.domain.port.in;

import com.sso.auth.domain.model.User;

/**
 * Puerto de entrada (Use Case): Registro de usuario.
 *
 * <p>Define el contrato que la capa de aplicación debe implementar
 * para registrar un nuevo usuario. Los adaptadores REST (Controller)
 * dependen de esta interfaz, no de la implementación concreta.
 *
 * <p><b>Tidy First:</b> Esta interfaz es un cambio estructural puro.
 * No contiene lógica, solo define el contrato del caso de uso.
 */
public interface RegisterUserUseCase {

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param command Datos necesarios para el registro
     * @return El usuario creado (con ID asignado)
     * @throws UsernameAlreadyExistsException si el username ya está en uso
     */
    User register(RegisterCommand command);

    /**
     * Command object con los datos de entrada para el registro.
     * Usar un Command en lugar de parámetros sueltos hace la firma
     * del método resistente a cambios futuros.
     */
    record RegisterCommand(
            String username,
            String rawPassword,    // Texto plano — se hashea en el servicio
            String fullName,
            String profilePhotoUrl
    ) {}

    /** Excepción de dominio: el username ya existe */
    class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException(String username) {
            super("El usuario '" + username + "' ya está registrado.");
        }
    }
}
