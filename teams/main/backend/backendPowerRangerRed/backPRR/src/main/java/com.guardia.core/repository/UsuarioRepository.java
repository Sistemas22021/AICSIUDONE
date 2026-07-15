package com.guardia.core.repository;

import com.guardia.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repositorio JPA para usuarios del sistema.
 * Soporta búsquedas por identificación o correo y operaciones de existencia.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByIdentificacion(String identificacion);
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByIdentificacion(String identificacion);
    boolean existsByCorreo(String correo);
}
