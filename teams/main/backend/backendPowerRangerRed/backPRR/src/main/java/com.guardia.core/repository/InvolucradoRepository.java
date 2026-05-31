package com.guardia.core.repository;

import com.guardia.core.model.Involucrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvolucradoRepository extends JpaRepository<Involucrado, Long> {
    List<Involucrado> findByIdentificacion(String identificacion);
}