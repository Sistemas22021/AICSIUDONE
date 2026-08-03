package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.UsuarioBalisticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioBalisticoRepository extends JpaRepository<UsuarioBalisticoEntity, Long> {
    Optional<UsuarioBalisticoEntity> findByUsername(String username);
}
