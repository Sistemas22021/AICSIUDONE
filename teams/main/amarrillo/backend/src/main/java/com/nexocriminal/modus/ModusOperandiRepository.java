package com.nexocriminal.modus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModusOperandiRepository extends JpaRepository<ModusOperandi, Long> {
    List<ModusOperandi> findByActivoTrueOrderByEtiquetaAsc();
    Optional<ModusOperandi> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}