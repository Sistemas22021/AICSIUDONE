package com.guardia.core.repository;
import com.guardia.core.model.enums.TipoRol;

import java.util.List;
import java.util.Optional;
import com.guardia.core.model.Involucrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repositorio JPA para involucrados en expedientes.
 * Provee consultas por expediente, rol y verificación por identificación+rol.
 */
public interface InvolucradoRepository extends JpaRepository<Involucrado, Long> {

    List<Involucrado> findByExpedienteId(Long expedienteId);

    List<Involucrado> findByExpedienteIdAndRol(Long expedienteId, TipoRol rol);

    boolean existsByIdentificacionAndRol(String identificacion, TipoRol rol);

    Optional<Involucrado> findByIdentificacionAndRol(
            String identificacion,
            TipoRol rol
    );

    List<Involucrado> findByRol(TipoRol rol);
}