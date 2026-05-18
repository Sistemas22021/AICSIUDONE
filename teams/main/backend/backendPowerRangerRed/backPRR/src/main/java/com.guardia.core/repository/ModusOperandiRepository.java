package com.guardia.core.repository;

import com.guardia.core.model.ModusOperandi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModusOperandiRepository extends JpaRepository<ModusOperandi, Long> {
    List<ModusOperandi> findByNivelConfianza(String nivelConfianza);
    List<ModusOperandi> findByPatronDetectadoContainingIgnoreCase(String patron);
}
