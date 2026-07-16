package com.nexocriminal.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionMotorRepository extends JpaRepository<ConfiguracionMotor, Long> {
}