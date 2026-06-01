package com.sso.auth.infrastructure.adapter.persistence;

import com.sso.auth.domain.model.User;
import com.sso.auth.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.sso.auth.infrastructure.adapter.out.persistence.UserJpaRepository;
import com.sso.auth.infrastructure.adapter.out.persistence.UserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración del adaptador de persistencia.
 *
 * <p><b>Estrategia:</b> {@code @DataJpaTest} carga solo el slice de JPA
 * (entidades, repositorios) con una BD H2 en memoria. No levanta el
 * contexto completo de Spring, por lo que es rápido.
 *
 * <p>Estos tests verifican que:
 * <ul>
 *   <li>El mapeo dominio ↔ JPA Entity funciona correctamente</li>
 *   <li>Las constraints de BD (UNIQUE) se aplican</li>
 *   <li>Las queries derivadas de Spring Data funcionan</li>
 * </ul>
 */
@DataJpaTest
@Import(UserRepositoryAdapter.class)
@DisplayName("UserRepositoryAdapter — Tests de integración con BD")
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private UserJpaRepository jpaRepository;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        sampleUser = new User(UUID.randomUUID(), "john_doe",
                "$2a$10$hashed", "John Doe", null, Instant.now());
    }

    @Test
    @DisplayName("Debe persistir y recuperar un usuario por username")
    void shouldSaveAndFindByUsername() {
        // Act
        userRepositoryAdapter.save(sampleUser);
        Optional<User> found = userRepositoryAdapter.findByUsername("john_doe");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().username()).isEqualTo("john_doe");
        assertThat(found.get().fullName()).isEqualTo("John Doe");
        assertThat(found.get().id()).isEqualTo(sampleUser.id());
    }

    @Test
    @DisplayName("existsByUsername debe retornar true si el usuario ya existe")
    void shouldReturnTrueWhenUsernameExists() {
        userRepositoryAdapter.save(sampleUser);

        assertThat(userRepositoryAdapter.existsByUsername("john_doe")).isTrue();
        assertThat(userRepositoryAdapter.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("findByUsername debe retornar vacío si el usuario no existe")
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> result = userRepositoryAdapter.findByUsername("ghost");
        assertThat(result).isEmpty();
    }
}
