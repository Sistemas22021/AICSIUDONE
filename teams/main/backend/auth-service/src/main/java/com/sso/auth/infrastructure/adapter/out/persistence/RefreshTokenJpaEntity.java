package com.sso.auth.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entidad JPA: representa la tabla {@code refresh_tokens} en PostgreSQL.
 *
 * <p>Almacena los tokens opacos de refresco para poder invalidarlos
 * explícitamente en el logout o cuando expiran.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_value", columnList = "token", unique = true),
        @Index(name = "idx_refresh_token_username", columnList = "username")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token; // UUID aleatorio

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
