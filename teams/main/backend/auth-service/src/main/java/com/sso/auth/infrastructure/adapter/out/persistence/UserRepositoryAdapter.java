package com.sso.auth.infrastructure.adapter.out.persistence;

import com.sso.auth.domain.model.User;
import com.sso.auth.domain.port.out.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto {@code UserRepository} del dominio
 * usando Spring Data JPA.
 *
 * <p>Esta clase es el puente entre el dominio y JPA. Convierte entre
 * {@code User} (dominio puro) y {@code UserJpaEntity} (entidad JPA).
 *
 * <p><b>Tidy First:</b> Los métodos de mapeo (toJpa / toDomain) están
 * separados y nombrados claramente para facilitar la lectura.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toJpa(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mappers privados: dominio ↔ JPA entity
    // ─────────────────────────────────────────────────────────────────────────

    private UserJpaEntity toJpa(User user) {
        return UserJpaEntity.builder()
                .id(user.id())
                .username(user.username())
                .password(user.password())
                .fullName(user.fullName())
                .profilePhotoUrl(user.profilePhotoUrl())
                .createdAt(user.createdAt())
                .build();
    }

    private User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getFullName(),
                entity.getProfilePhotoUrl(),
                entity.getCreatedAt()
        );
    }
}
