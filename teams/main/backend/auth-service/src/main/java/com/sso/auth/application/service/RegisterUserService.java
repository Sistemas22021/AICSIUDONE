package com.sso.auth.application.service;

import com.sso.auth.domain.model.User;
import com.sso.auth.domain.port.in.RegisterUserUseCase;
import com.sso.auth.domain.port.out.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Registro de usuario.
 *
 * <p>Esta clase pertenece a la capa de <b>Aplicación</b> de la arquitectura hexagonal.
 * Su única responsabilidad es orquestar la lógica del caso de uso:
 * verificar duplicados, hashear la contraseña y delegar la persistencia al puerto.
 *
 * <p><b>TDD — Ciclo aplicado:</b>
 * <ol>
 *   <li>RED: Se escribió {@code RegisterUserServiceTest} con el test de username duplicado</li>
 *   <li>GREEN: Se implementó el mínimo código para pasar el test</li>
 *   <li>REFACTOR: Se extrajo la lógica de creación a {@code User.newUser()}</li>
 * </ol>
 *
 * <p><b>Tidy First:</b> Esta clase no tiene dependencias de Spring Boot más allá de
 * {@code @Service}. No importa nada de JPA ni de Web. Eso la hace testeable con Mockito puro.
 */
@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección por constructor: facilita los tests (no se necesita @MockBean)
    public RegisterUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterCommand command) {
        // Regla de negocio: el username debe ser único
        if (userRepository.existsByUsername(command.username())) {
            throw new UsernameAlreadyExistsException(command.username());
        }

        // El hash se hace aquí, en la capa de aplicación, no en el dominio ni en la infra
        String hashedPassword = passwordEncoder.encode(command.rawPassword());

        User newUser = User.newUser(
                command.username(),
                hashedPassword,
                command.fullName(),
                command.profilePhotoUrl()
        );

        return userRepository.save(newUser);
    }
}
