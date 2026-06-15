package com.guardia.core.repository;

import com.guardia.core.model.Denunciante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repositorio JPA para denunciantes.
 * Soporta búsqueda por identificación y comprobación de existencia.
 */
public interface DenuncianteRepository extends JpaRepository<Denunciante, Long> {
    Optional<Denunciante> findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
}
