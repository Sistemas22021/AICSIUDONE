package com.guardia.core.repository;

import com.guardia.core.model.FirmaConductual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FirmaConductualRepository extends JpaRepository<FirmaConductual, Long> {

    List<FirmaConductual> findByExpedienteIdOrderByVersionDesc(Long expedienteId);
    Optional<FirmaConductual> findByExpedienteIdAndVigenteTrue(Long expedienteId);
}
//Para el push