package com.nexocriminal.engine;

import com.nexocriminal.domain.alerta.*;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecida;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecidaRepository;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.ubicacion.UbicacionRepository;
import com.nexocriminal.domain.vinculo.Vinculo;
import com.nexocriminal.domain.vinculo.VinculoRepository;
import com.nexocriminal.config.ConfiguracionMotorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Regla 5: Cluster de desapariciones.
 *
 * Detecta:
 * 1. Múltiples desapariciones en un radio geográfico cercano (cluster).
 * 2. Desapariciones cuya última ubicación está cerca de un nodo sospechoso.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReglaClusterDesapariciones implements ReglaVinculo {

    private final PersonaDesaparecidaRepository desaparecidaRepository;
    private final UbicacionRepository ubicacionRepository;
    private final VinculoRepository vinculoRepository;
    private final AlertaRepository alertaRepository;
    private final ConfiguracionMotorService configService;

    @Override
    public String nombre() {
        return "REGLA_CLUSTER_DESAPARICIONES";
    }

    @Override
    public ResultadoRegla ejecutar() {
        var cfg = configService.obtener();
        int radioCluster = cfg.getClusterRadioMetros();
        int minCluster = cfg.getClusterMin();
        int ventanaDias = cfg.getClusterVentanaDias();
        int radioNodoSospechoso = cfg.getClusterRadioNodoSospechoso();

        log.info("Ejecutando {} (radio cluster={}m, min={}, ventana={}d)",
                nombre(), radioCluster, minCluster, ventanaDias);

        List<Vinculo> vinculos = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();

        LocalDateTime desde = LocalDateTime.now().minusDays(ventanaDias);

        List<PersonaDesaparecida> activas = desaparecidaRepository.findByEstado(EstadoDesaparicion.BUSCADA)
                .stream()
                .filter(p -> p.getFechaDesaparicion() != null && p.getFechaDesaparicion().isAfter(desde))
                .filter(p -> p.getUltimaUbicacion() != null)
                .toList();

        // -------- 1. CLUSTER: detectar grupos cercanos --------
        List<Long> procesadas = new ArrayList<>();

        for (PersonaDesaparecida pivote : activas) {
            if (procesadas.contains(pivote.getId())) continue;

            Ubicacion ubP = pivote.getUltimaUbicacion();
            List<PersonaDesaparecida> cluster = new ArrayList<>();
            cluster.add(pivote);

            for (PersonaDesaparecida otra : activas) {
                if (otra.getId().equals(pivote.getId())) continue;
                if (procesadas.contains(otra.getId())) continue;

                double dist = distanciaMetros(
                        ubP.getLatitud(), ubP.getLongitud(),
                        otra.getUltimaUbicacion().getLatitud(),
                        otra.getUltimaUbicacion().getLongitud()
                );

                if (dist <= radioCluster) {
                    cluster.add(otra);
                }
            }

            if (cluster.size() >= minCluster) {
                cluster.forEach(p -> procesadas.add(p.getId()));

                for (int i = 0; i < cluster.size(); i++) {
                    for (int j = i + 1; j < cluster.size(); j++) {
                        if (existeDuplicado("DESAPARECIDA", cluster.get(i).getId(),
                                "DESAPARECIDA", cluster.get(j).getId(), nombre())) continue;

                        Vinculo v = Vinculo.builder()
                                .tipoOrigen("DESAPARECIDA").idOrigen(cluster.get(i).getId())
                                .tipoDestino("DESAPARECIDA").idDestino(cluster.get(j).getId())
                                .reglaDetectada(nombre())
                                .score(BigDecimal.valueOf(0.85))
                                .build();
                        vinculos.add(vinculoRepository.save(v));
                    }
                }

                StringBuilder titulo = new StringBuilder("Cluster de desapariciones detectado: ");
                titulo.append(cluster.size()).append(" personas en zona común");

                StringBuilder desc = new StringBuilder();
                desc.append("Se detectaron ").append(cluster.size())
                        .append(" desapariciones en un radio de ").append(radioCluster)
                        .append("m, dentro de una ventana de ").append(ventanaDias).append(" días. ");
                desc.append("Personas: ");
                for (int i = 0; i < cluster.size(); i++) {
                    if (i > 0) desc.append(", ");
                    PersonaDesaparecida p = cluster.get(i);
                    desc.append(p.getNombre()).append(" ").append(p.getApellido());
                }

                Alerta a = Alerta.builder()
                        .tipo(TipoAlerta.CLUSTER_DESAPARICIONES)
                        .titulo(titulo.toString())
                        .descripcion(desc.toString())
                        .nivelRiesgo(NivelRiesgo.CRITICO)
                        .build();
                alertas.add(alertaRepository.save(a));
            }
        }

        // -------- 2. DESAPARICIÓN CERCA DE NODO SOSPECHOSO --------
        List<Ubicacion> nodosSospechosos = ubicacionRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getNodoSospechoso()))
                .toList();

        for (PersonaDesaparecida pd : activas) {
            Ubicacion ubP = pd.getUltimaUbicacion();
            for (Ubicacion nodo : nodosSospechosos) {
                double dist = distanciaMetros(
                        ubP.getLatitud(), ubP.getLongitud(),
                        nodo.getLatitud(), nodo.getLongitud()
                );

                if (dist <= radioNodoSospechoso) {
                    if (existeDuplicado("DESAPARECIDA", pd.getId(),
                            "UBICACION", nodo.getId(), nombre())) continue;

                    Vinculo v = Vinculo.builder()
                            .tipoOrigen("DESAPARECIDA").idOrigen(pd.getId())
                            .tipoDestino("UBICACION").idDestino(nodo.getId())
                            .reglaDetectada(nombre())
                            .score(BigDecimal.valueOf(0.95))
                            .build();
                    vinculos.add(vinculoRepository.save(v));

                    Alerta a = Alerta.builder()
                            .tipo(TipoAlerta.DESAPARICION_NODO_SOSPECHOSO)
                            .titulo("Desaparición cerca de nodo sospechoso")
                            .descripcion("La última ubicación de " + pd.getNombre() + " " + pd.getApellido() +
                                    " está a " + Math.round(dist) + "m de la ubicación sospechosa: " +
                                    (nodo.getDireccion() != null ? nodo.getDireccion() : "ID #" + nodo.getId()))
                            .nivelRiesgo(NivelRiesgo.ALTO)
                            .build();
                    alertas.add(alertaRepository.save(a));
                }
            }
        }

        log.info("{} completada: {} vínculos, {} alertas", nombre(), vinculos.size(), alertas.size());
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