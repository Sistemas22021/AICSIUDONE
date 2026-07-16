package com.guardia.core.repository;

import com.guardia.core.model.Caso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long> {

    Optional<Caso> findByCodigoCaso(String codigoCaso);

    boolean existsByCodigoCaso(String codigoCaso);

    @Query("SELECT COUNT(c) FROM Caso c")
    long contarCasos();
}