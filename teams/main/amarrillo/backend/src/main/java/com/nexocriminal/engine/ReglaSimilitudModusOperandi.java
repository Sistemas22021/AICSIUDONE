package com.nexocriminal.engine;

import com.nexocriminal.domain.alerta.Alerta;
import com.nexocriminal.domain.alerta.NivelRiesgo;
import com.nexocriminal.domain.alerta.TipoAlerta;
import com.nexocriminal.domain.suceso.Suceso;
import com.nexocriminal.domain.suceso.SucesoRepository;
import com.nexocriminal.domain.vinculo.Vinculo;
import com.nexocriminal.domain.vinculo.VinculoRepository;
import com.nexocriminal.config.ConfiguracionMotorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * PLOT TWIST: Similitud de Modus Operandi.
 *
 * Calcula un índice de similitud entre pares de sucesos basado en:
 *   - Mismo tipo de suceso (peso 0.3)
 *   - Mismo modus_operandi (peso 0.5)
 *   - Franja horaria cercana +/- 2h (peso 0.2)
 *
 * Si el score >= umbral, se enhebra un vínculo que sugiere
 * que los sucesos pertenecen a la misma banda criminal.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReglaSimilitudModusOperandi implements ReglaVinculo {

    private final SucesoRepository sucesoRepository;
    private final VinculoRepository vinculoRepository;
    private final JpaRepository<Alerta, Long> alertaRepository;
    private final ConfiguracionMotorService configService;

    @Override
    public String nombre() {
        return "REGLA_MODUS_OPERANDI";
    }

    @Override
    public ResultadoRegla ejecutar() {
        var cfg = configService.obtener();
        double umbral = cfg.getModusUmbral();

        log.info("Ejecutando {} (umbral={})", nombre(), umbral);

        List<Vinculo> vinculos = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();
        List<Suceso> sucesos = sucesoRepository.findAll();

        for (int i = 0; i < sucesos.size(); i++) {
            for (int j = i + 1; j < sucesos.size(); j++) {
                Suceso a = sucesos.get(i);
                Suceso b = sucesos.get(j);
                double score = similitud(a, b);

                if (score >= umbral) {
                    if (!vinculoRepository.findDuplicado("SUCESO", a.getId(),
                            "SUCESO", b.getId(), nombre()).isEmpty()) continue;

                    Vinculo v = Vinculo.builder()
                            .tipoOrigen("SUCESO").idOrigen(a.getId())
                            .tipoDestino("SUCESO").idDestino(b.getId())
                            .reglaDetectada(nombre())
                            .score(BigDecimal.valueOf(score))
                            .build();
                    vinculos.add(vinculoRepository.save(v));

                    Alerta alerta = Alerta.builder()
                            .tipo(TipoAlerta.MODUS_OPERANDI)
                            .titulo("Patron de banda detectado")
                            .descripcion(String.format(
                                    "Los sucesos #%d y #%d comparten modus operandi '%s' con score %.2f.",
                                    a.getId(), b.getId(),
                                    a.getModusOperandi() != null ? a.getModusOperandi() : "N/A",
                                    score))
                            .nivelRiesgo(score >= 0.9 ? NivelRiesgo.CRITICO : NivelRiesgo.ALTO)
                            .build();
                    alertas.add(alertaRepository.save(alerta));
                }
            }
        }

        log.info("{} completada: {} vinculos, {} alertas",
                nombre(), vinculos.size(), alertas.size());
        return new ResultadoRegla(nombre(), vinculos, alertas);
    }

    private double similitud(Suceso a, Suceso b) {
        double score = 0.0;

        // Mismo tipo de suceso (0.3)
        if (a.getTipo() == b.getTipo()) {
            score += 0.3;
        }

        // Mismo modus operandi (0.5)
        if (a.getModusOperandi() != null && a.getModusOperandi().equals(b.getModusOperandi())) {
            score += 0.5;
        }

        // Franja horaria cercana (0.2)
        if (a.getFechaHora() != null && b.getFechaHora() != null) {
            int horaA = a.getFechaHora().getHour();
            int horaB = b.getFechaHora().getHour();
            int diff = Math.min(Math.abs(horaA - horaB), 24 - Math.abs(horaA - horaB));
            if (diff <= 2) {
                score += 0.2;
            }
        }

        return score;
    }
}
