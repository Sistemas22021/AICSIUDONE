package com.sso.auth.domain.port.out;

import com.sso.auth.domain.model.User;

import java.util.Optional;

/**
 * Puerto de salida (Repository): Usuarios.
 *
 * <p>Define el contrato para persistir y recuperar usuarios.
 * La implementación concreta vive en la capa de infraestructura
 * ({@code UserRepositoryAdapter}), usando Spring Data JPA.
 *
 * <p><b>¿Por qué usar una interfaz aquí?</b><br>
 * Permite testear los casos de uso con mocks (Mockito) sin necesitar
 * una base de datos. El dominio define QUÉ necesita, la infraestructura
 * define CÓMO lo hace.
 */
public interface UserRepository {

    /**
     * Persiste un usuario nuevo. Lanza excepción si el username ya existe
     * (constraint unique en BD).
     */
    User save(User user);

    /** Busca un usuario por su username. */
    Optional<User> findByUsername(String username);

    /** Verifica si un username ya está en uso (evita cargar el objeto completo). */
    boolean existsByUsername(String username);
}
