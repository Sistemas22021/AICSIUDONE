package com.sso.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio: Usuario.
 *
 * <p><b>Decisión de diseño (Hexagonal Architecture):</b><br>
 * Esta clase es un {@code record} de Java 17 puro, sin ninguna anotación
 * de JPA, Spring o de ningún framework. Esto garantiza que el dominio
 * sea independiente de la infraestructura y pueda testearse sin Spring.
 *
 * <p>La persistencia se maneja en {@code UserJpaEntity} (capa de infraestructura),
 * que se mapea a/desde este objeto mediante el {@code UserRepositoryAdapter}.
 *
 * <p><b>Campos mínimos requeridos por el enunciado:</b>
 * <ul>
 *   <li>{@code username} — identificador único del usuario</li>
 *   <li>{@code password} — hash BCrypt (nunca el texto plano)</li>
 *   <li>{@code fullName} — nombre completo</li>
 *   <li>{@code profilePhotoUrl} — URL de la foto de perfil</li>
 * </ul>
 */
public record User(
        UUID id,
        String username,
        String password,       // Siempre hasheado con BCrypt. Nunca texto plano.
        String fullName,
        String profilePhotoUrl,
        Instant createdAt
) {
    /**
     * Factory method para crear un nuevo usuario antes de persistirlo.
     * El ID y la fecha de creación se asignan aquí para mantener
     * esa responsabilidad en el dominio, no en la base de datos.
     */
    public static User newUser(String username, String hashedPassword,
                               String fullName, String profilePhotoUrl) {
        return new User(
                UUID.randomUUID(),
                username,
                hashedPassword,
                fullName,
                profilePhotoUrl,
                Instant.now()
        );
    }
}
