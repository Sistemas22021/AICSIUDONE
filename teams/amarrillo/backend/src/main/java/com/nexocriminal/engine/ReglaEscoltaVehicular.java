package com.nexocriminal.engine;

import com.nexocriminal.domain.alerta.Alerta;
import com.nexocriminal.domain.alerta.NivelRiesgo;
import com.nexocriminal.domain.alerta.TipoAlerta;
import com.nexocriminal.domain.vehiculo.Vehiculo;
import com.nexocriminal.domain.vehiculo.VehiculoRepository;
import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.domain.vinculo.Vinculo;
import com.nexocriminal.domain.vinculo.VinculoRepository;
import com.nexocriminal.config.ConfiguracionMotorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CU-B: Detectar vehiculos que acompañan repetidamente a vehiculos robados.
 *
 * Logica (simplificada para el MVP):
 *   1. Por cada vehiculo robado, obtener sus avistamientos.
 *   2. Para cada avistamiento, buscar otros vehiculos avistados en la misma
 *      ubicacion dentro de una ventana de +/-N minutos.
 *   3. Si un mismo vehiculo X aparece >= M veces cerca del robado,
 *      marcarlo como VEHICULO_APOYO y crear vinculo + alerta.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReglaEscoltaVehicular implements ReglaVinculo {

    @PersistenceContext
    private EntityManager em;

    private final VehiculoRepository vehiculoRepository;
    private final VinculoRepository vinculoRepository;
    private final JpaRepository<Alerta, Long> alertaRepository;
    private final ConfiguracionMotorService configService;

    @Override
    public String nombre() {
        return "REGLA_ESCOLTA_VEHICULAR";
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultadoRegla ejecutar() {
        var cfg = configService.obtener();
        int ventanaMinutos = cfg.getEscoltaVentanaMinutos();
        int minCoincidencias = cfg.getEscoltaMinCoincidencias();

        log.info("Ejecutando {} (ventana={}min, minCoincidencias={})",
                nombre(), ventanaMinutos, minCoincidencias);

        List<Vinculo> vinculos = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();

        List<Vehiculo> robados = vehiculoRepository.findByEstado(EstadoVehiculo.ROBADO);

        for (Vehiculo robado : robados) {
            // Para cada vehiculo robado, contar cuantas veces otros vehiculos
            // fueron avistados en la misma ubicacion +/- ventana
            String sql = """
                SELECT a2.vehiculo_id, COUNT(*) AS coincidencias
                FROM avistamiento a1
                JOIN avistamiento a2
                  ON a1.ubicacion_id = a2.ubicacion_id
                 AND a1.vehiculo_id <> a2.vehiculo_id
                 AND ABS(EXTRACT(EPOCH FROM (a1.fecha_hora - a2.fecha_hora))) <= :ventanaSeg
                WHERE a1.vehiculo_id = :robadoId
                GROUP BY a2.vehiculo_id
                HAVING COUNT(*) >= :minCoincidencias
                """;
            List<Object[]> filas = em.createNativeQuery(sql)
                    .setParameter("robadoId", robado.getId())
                    .setParameter("ventanaSeg", ventanaMinutos * 60)
                    .setParameter("minCoincidencias", minCoincidencias)
                    .getResultList();

            for (Object[] fila : filas) {
                Long apoyoId = ((Number) fila[0]).longValue();
                long coincidencias = ((Number) fila[1]).longValue();
                if (!vinculoRepository.findDuplicado("VEHICULO", robado.getId(),
                        "VEHICULO", apoyoId, nombre()).isEmpty()) continue;

                // Marcar vehiculo sospechoso como VEHICULO_APOYO
                Vehiculo apoyo = vehiculoRepository.findById(apoyoId).orElse(null);
                if (apoyo != null && apoyo.getEstado() == EstadoVehiculo.NORMAL) {
                    apoyo.setEstado(EstadoVehiculo.VEHICULO_APOYO);
                    vehiculoRepository.save(apoyo);
                }

                Vinculo v = Vinculo.builder()
                        .tipoOrigen("VEHICULO").idOrigen(robado.getId())
                        .tipoDestino("VEHICULO").idDestino(apoyoId)
                        .reglaDetectada(nombre())
                        .score(BigDecimal.valueOf(Math.min(1.0, coincidencias / 5.0)))
                        .build();
                vinculos.add(vinculoRepository.save(v));

                Alerta a = Alerta.builder()
                        .tipo(TipoAlerta.VEHICULO_APOYO)
                        .titulo("Vehiculo de apoyo detectado: placa " +
                                (apoyo != null ? apoyo.getPlaca() : "#" + apoyoId))
                        .descripcion("Acompaño al vehiculo " + robado.getPlaca() +
                                " en " + coincidencias + " ocasiones cercanas.")
                        .nivelRiesgo(NivelRiesgo.MEDIO)
                        .build();
                alertas.add(alertaRepository.save(a));
            }
        }

        log.info("{} completada: {} vinculos, {} alertas", nombre(), vinculos.size(), alertas.size());
        return new ResultadoRegla(nombre(), vinculos, alertas);
    }
}
