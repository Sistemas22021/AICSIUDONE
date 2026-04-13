package com.sso.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del Auth Service.
 *
 * <p>Microservicio responsable de:
 * <ul>
 *   <li>Gestión de usuarios (registro, consulta)</li>
 *   <li>Autenticación (login, logout)</li>
 *   <li>Generación y validación de tokens JWT</li>
 *   <li>Gestión de Refresh Tokens (persistencia en BD)</li>
 * </ul>
 *
 * <p><b>Arquitectura:</b> Hexagonal (Ports & Adapters)
 * <ul>
 *   <li>{@code domain/} — Entidades y puertos. Sin dependencias de frameworks.</li>
 *   <li>{@code application/} — Casos de uso. Orquesta el dominio.</li>
 *   <li>{@code infrastructure/} — Adaptadores REST, JPA, configuración Spring.</li>
 * </ul>
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
