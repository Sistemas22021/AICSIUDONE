package com.guardia.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.guardia.core.model.tipoDelito;

@Repository
public interface tipoDelitoRepository extends JpaRepository<tipoDelito, Long> {

}
