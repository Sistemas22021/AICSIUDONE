package com.azulcian.GestionIncidentesPatrullas.patrol.repository;

import com.azulcian.GestionIncidentesPatrullas.patrol.model.Patrol;
import com.azulcian.GestionIncidentesPatrullas.patrol.model.PatrolStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatrolRepository extends JpaRepository<Patrol, Long> {

    // =========================================
    // FIND BY STATUS
    // =========================================
    List<Patrol> findByStatus(PatrolStatus status);
}