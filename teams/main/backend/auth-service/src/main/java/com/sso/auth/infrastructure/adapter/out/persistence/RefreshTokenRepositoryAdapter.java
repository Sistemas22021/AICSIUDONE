package com.sso.auth.infrastructure.adapter.out.persistence;

import com.sso.auth.domain.model.RefreshToken;
import com.sso.auth.domain.port.out.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto {@code RefreshTokenRepository}
 * del dominio usando Spring Data JPA.
 *
 * <p>Esta clase es el puente entre el dominio y JPA. Convierte entre
 * {@code RefreshToken} (dominio puro) y {@code RefreshTokenJpaEntity}.
 *
 * <p><b>Tidy First:</b> Los métodos de mapeo (toJpa / toDomain) están
 * separados y nombrados claramente para facilitar la lectura.
 */
@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = toJpa(refreshToken);
        RefreshTokenJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public void deleteByUsername(String username) {
        jpaRepository.deleteByUsername(username);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mappers privados: dominio ↔ JPA entity
    // ─────────────────────────────────────────────────────────────────────────

    private RefreshTokenJpaEntity toJpa(RefreshToken refreshToken) {
        return RefreshTokenJpaEntity.builder()
                .token(refreshToken.token())
                .username(refreshToken.username())
                .expiresAt(refreshToken.expiresAt())
                .build();
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(
                entity.getToken(),
                entity.getUsername(),
                entity.getExpiresAt()
        );
    }
}
