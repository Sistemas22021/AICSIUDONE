package com.nexocriminal.engine;

import com.nexocriminal.domain.alerta.*;
import com.nexocriminal.domain.suceso.Suceso;
import com.nexocriminal.domain.suceso.SucesoRepository;
import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.domain.ubicacion.TipoUbicacion;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.ubicacion.UbicacionRepository;
import com.nexocriminal.domain.ubicacion.UbicacionService;
import com.nexocriminal.domain.vinculo.Vinculo;
import com.nexocriminal.domain.vinculo.VinculoRepository;
import com.nexocriminal.config.ConfiguracionMotorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CU-A: Identificacion de Nodos Logisticos (deshuesaderos).
 *
 * Logica:
 *   1. Consultar sucesos de ROBO_VEHICULO ocurridos en la ventana de tiempo.
 *   2. Agrupar por proximidad a ubicaciones tipo TALLER/GALPON/TERRENO_BALDIO.
 *   3. Si una ubicacion tiene >= N vehiculos robados cerca, generar alerta.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReglaNodoLogistico implements ReglaVinculo {

    private final SucesoRepository sucesoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final UbicacionService ubicacionService;
    private final VinculoRepository vinculoRepository;
    private final JpaRepository<Alerta, Long> alertaRepository;
    private final ConfiguracionMotorService configService;

    @Override
    public String nombre() {
        return "REGLA_NODO_LOGISTICO";
    }

    @Override
    public ResultadoRegla ejecutar() {
        var cfg = configService.obtener();
        int radioMetros = cfg.getNodoRadioMetros();
        int ventanaHoras = cfg.getNodoVentanaHoras();
        int minVehiculos = cfg.getNodoMinVehiculos();

        log.info("Ejecutando {} (radio={}m, ventana={}h, minVehiculos={})",
                nombre(), radioMetros, ventanaHoras, minVehiculos);

        LocalDateTime desde = LocalDateTime.now().minusHours(ventanaHoras);
        List<Suceso> robos = sucesoRepository
                .findByTipoAndFechaHoraAfter(TipoSuceso.ROBO_VEHICULO, desde);

        List<Ubicacion> candidatas = ubicacionRepository.findByTipoIn(
                List.of(TipoUbicacion.TALLER, TipoUbicacion.GALPON, TipoUbicacion.TERRENO_BALDIO));

        List<Vinculo> vinculos = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();

        for (Ubicacion ubi : candidatas) {
            List<Suceso> cercanos = new ArrayList<>();
            for (Suceso s : robos) {
                Ubicacion refUbi = s.getUbicacionUltima() != null
                        ? s.getUbicacionUltima() : s.getUbicacion();
                if (refUbi == null) continue;
                double dist = distanciaMetros(ubi.getLatitud(), ubi.getLongitud(),
                        refUbi.getLatitud(), refUbi.getLongitud());
                if (dist <= radioMetros) {
                    cercanos.add(s);
                }
            }

            if (cercanos.size() >= minVehiculos) {
                ubicacionService.marcarComoNodoSospechoso(ubi.getId());
                for (Suceso s : cercanos) {
                    if (s.getVehiculo() == null) continue;
                    if (existeDuplicado("UBICACION", ubi.getId(), "VEHICULO",
                            s.getVehiculo().getId(), nombre())) continue;

                    Vinculo v = Vinculo.builder()
                            .tipoOrigen("UBICACION").idOrigen(ubi.getId())
                            .tipoDestino("VEHICULO").idDestino(s.getVehiculo().getId())
                            .reglaDetectada(nombre())
                            .score(BigDecimal.valueOf(0.90))
                            .build();
                    vinculos.add(vinculoRepository.save(v));
                }
                Alerta a = Alerta.builder()
                        .tipo(TipoAlerta.NODO_LOGISTICO)
                        .titulo("Posible nodo logistico: " +
                                (ubi.getDireccion() != null ? ubi.getDireccion() : "Ubicacion #" + ubi.getId()))
                        .descripcion("Se detectaron " + cercanos.size() +
                                " vehiculos robados en un radio de " + radioMetros + "m.")
                        .nivelRiesgo(NivelRiesgo.ALTO)
                        .build();
                alertas.add(alertaRepository.save(a));
            }
        }

        log.info("{} completada: {} vinculos, {} alertas", nombre(), vinculos.size(), alertas.size());
        return new ResultadoRegla(nombre(), vinculos, alertas);
    }

    private boolean existeDuplicado(String tipoOri, Long idOri, String tipoDest, Long idDest, String regla) {
        return !vinculoRepository.findDuplicado(tipoOri, idOri, tipoDest, idDest, regla).isEmpty();
    }

    /** Distancia en metros (Haversine). */
    static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
