package com.nexocriminal.domain.relacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelacionRepository extends JpaRepository<Relacion, Long> {

    @Query("SELECT r FROM Relacion r WHERE r.personaA.id = :personaId OR r.personaB.id = :personaId")
    List<Relacion> findByPersonaId(@Param("personaId") Long personaId);
}
