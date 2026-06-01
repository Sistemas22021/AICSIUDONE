package com.sso.auth.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para {@code UserJpaEntity}.
 *
 * <p>Spring genera la implementación en tiempo de compilación/runtime.
 * No se necesita código adicional para las operaciones básicas.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
