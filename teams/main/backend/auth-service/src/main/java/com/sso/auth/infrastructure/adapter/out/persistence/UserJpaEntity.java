package com.sso.auth.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA: representa la tabla {@code users} en PostgreSQL.
 *
 * <p><b>Decisión de diseño:</b><br>
 * Esta clase está SEPARADA de {@code User} (dominio). Las anotaciones JPA
 * (@Entity, @Column, etc.) son detalles de infraestructura que no deben
 * contaminar el modelo de dominio puro.
 *
 * <p>El mapeo entre ambas clases es responsabilidad del adaptador
 * {@code UserRepositoryAdapter}.
 *
 * <p><b>Campos:</b>
 * <ul>
 *   <li>{@code username} — UNIQUE para garantizar un solo usuario por nombre</li>
 *   <li>{@code password} — Siempre almacenado como hash BCrypt</li>
 *   <li>{@code profile_photo_url} — URL externa (S3, Gravatar, etc.)</li>
 * </ul>
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username", columnList = "username", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // Hash BCrypt — nunca texto plano

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
