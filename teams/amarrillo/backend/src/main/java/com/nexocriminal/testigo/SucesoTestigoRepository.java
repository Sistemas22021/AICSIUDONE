package com.nexocriminal.testigo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucesoTestigoRepository extends JpaRepository<SucesoTestigo, Long> {
    List<SucesoTestigo> findBySucesoId(Long sucesoId);
    void deleteBySucesoId(Long sucesoId);
}