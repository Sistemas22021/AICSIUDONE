package com.nexocriminal.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea un usuario admin por defecto (admin / admin123) si no existe.
 * Util para que el MVP arranque listo para usarse.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapAdmin implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (repository.findByUsername("admin").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .passwordHash(encoder.encode("admin123"))
                    .nombreCompleto("Administrador")
                    .rol("ANALISTA")
                    .activo(true)
                    .build();
            repository.save(admin);
            log.info("=> Usuario 'admin' creado con password 'admin123'");
        }
    }
}
