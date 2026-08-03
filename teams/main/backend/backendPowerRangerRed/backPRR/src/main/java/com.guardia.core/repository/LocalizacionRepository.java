package com.guardia.core.repository;

import com.guardia.core.model.Localizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalizacionRepository extends JpaRepository<Localizacion, Long> {
    List<Localizacion> findByMunicipio(String municipio);
    List<Localizacion> findBySector(String sector);
}
