package com.nexocriminal.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea los usuarios de demostracion (uno por rol) si no existen.
 * Permite mostrar en la defensa como cambian los permisos segun el rol.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapAdmin implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        crearSiNoExiste("admin",      "admin123",      "Administrador del Sistema", "ADMIN");
        crearSiNoExiste("analista",   "analista123",   "Analista Criminal",         "ANALISTA");
        crearSiNoExiste("supervisor", "supervisor123", "Supervisor de Operaciones", "SUPERVISOR");
        crearSiNoExiste("auditor",    "auditor123",    "Auditor",                   "AUDITOR");
    }

    private void crearSiNoExiste(String username, String password, String nombre, String rol) {
        repository.findByUsername(username).ifPresentOrElse(
            existente -> {
                // Corrige el rol si quedo mal asignado en un arranque anterior
                if (!rol.equalsIgnoreCase(existente.getRol())) {
                    existente.setRol(rol);
                    repository.save(existente);
                    log.info("=> Rol de '{}' corregido a {}", username, rol);
                }
            },
            () -> {
                Usuario u = Usuario.builder()
                        .username(username)
                        .passwordHash(encoder.encode(password))
                        .nombreCompleto(nombre)
                        .rol(rol)
                        .activo(true)
                        .build();
                repository.save(u);
                log.info("=> Usuario '{}' creado (rol {}, password '{}')", username, rol, password);
            }
        );
    }
}