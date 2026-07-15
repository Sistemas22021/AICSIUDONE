package com.nexocriminal.domain.vinculo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VinculoRepository extends JpaRepository<Vinculo, Long> {

    @Query("""
        SELECT v FROM Vinculo v
        WHERE v.activo = true
          AND ((v.tipoOrigen = :tipo AND v.idOrigen = :id)
            OR (v.tipoDestino = :tipo AND v.idDestino = :id))
        """)
    List<Vinculo> findByNodo(@Param("tipo") String tipo, @Param("id") Long id);

    @Query("SELECT v FROM Vinculo v WHERE v.activo = true")
    List<Vinculo> findAllActivos();

    @Query("""
        SELECT v FROM Vinculo v
        WHERE v.tipoOrigen = :tipoOri AND v.idOrigen = :idOri
          AND v.tipoDestino = :tipoDest AND v.idDestino = :idDest
          AND v.reglaDetectada = :regla
        """)
    List<Vinculo> findDuplicado(@Param("tipoOri") String tipoOri,
                                @Param("idOri") Long idOri,
                                @Param("tipoDest") String tipoDest,
                                @Param("idDest") Long idDest,
                                @Param("regla") String regla);
}
