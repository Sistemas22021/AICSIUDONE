package com.nexocriminal.domain.suceso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SucesoRepository extends JpaRepository<Suceso, Long> {

    List<Suceso> findByTipoAndFechaHoraAfter(TipoSuceso tipo, LocalDateTime desde);

    List<Suceso> findByTipo(TipoSuceso tipo);

    List<Suceso> findByModusOperandi(String modusOperandi);
}
