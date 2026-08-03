package com.guardia.core.repository;

import com.guardia.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByIdentificacion(String identificacion);
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByIdentificacion(String identificacion);
    boolean existsByCorreo(String correo);
}
