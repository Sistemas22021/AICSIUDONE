package com.guardia.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.guardia.core.model.TipoDelito;

import java.util.Optional;

@Repository
public interface TipoDelitoRepository extends JpaRepository<TipoDelito, Long> {

    // Spring entiende que debe hacer: SELECT * FROM tipos_delitos WHERE nombre = ?
    boolean existsByNombreIgnoreCase(String nombre);

    // O si quieres obtener el objeto completo:
    Optional<TipoDelito> findByNombre(String nombre);

}
