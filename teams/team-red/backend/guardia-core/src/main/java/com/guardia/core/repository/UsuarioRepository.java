package com.guardia.core.repository;

import com.guardia.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
/**
 * Repositorio JPA para usuarios del sistema.
 * Soporta búsquedas por identificación o correo y operaciones de existencia.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}
