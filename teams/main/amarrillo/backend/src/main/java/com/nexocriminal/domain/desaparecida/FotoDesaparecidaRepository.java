package com.nexocriminal.domain.desaparecida;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoDesaparecidaRepository extends JpaRepository<FotoDesaparecida, Long> {

    List<FotoDesaparecida> findByPersonaDesaparecidaIdOrderByOrdenAsc(Long personaId);

    long countByPersonaDesaparecidaId(Long personaId);

    void deleteByPersonaDesaparecidaId(Long personaId);
}