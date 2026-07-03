package com.nexocriminal.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquestador del Red Thread Engine.
 * Ejecuta todas las reglas registradas y consolida los resultados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedThreadEngineService {

    private final ReglaNodoLogistico reglaNodoLogistico;
    private final ReglaEscoltaVehicular reglaEscolta;
    private final ReglaCirculoConfianza reglaCirculo;
    private final ReglaSimilitudModusOperandi reglaModus;
    private final ReglaClusterDesapariciones reglaClusterDesapariciones;

    @Transactional
    public List<ReglaVinculo.ResultadoRegla> ejecutarTodas() {
        log.info("=== EJECUTANDO RED THREAD ENGINE ===");
        List<ReglaVinculo.ResultadoRegla> resultados = new ArrayList<>();
        resultados.add(reglaNodoLogistico.ejecutar());
        resultados.add(reglaEscolta.ejecutar());
        resultados.add(reglaCirculo.ejecutar());
        resultados.add(reglaModus.ejecutar());
        resultados.add(reglaClusterDesapariciones.ejecutar());
        log.info("=== RED THREAD ENGINE COMPLETADO ===");
        return resultados;
    }

    @Transactional
    public ReglaVinculo.ResultadoRegla ejecutarNodoLogistico() {
        return reglaNodoLogistico.ejecutar();
    }

    @Transactional
    public ReglaVinculo.ResultadoRegla ejecutarEscolta() {
        return reglaEscolta.ejecutar();
    }

    @Transactional
    public ReglaVinculo.ResultadoRegla ejecutarCirculoConfianza() {
        return reglaCirculo.ejecutar();
    }

    @Transactional
    public ReglaVinculo.ResultadoRegla ejecutarModusOperandi() {
        return reglaModus.ejecutar();
    }

    @Transactional
    public ReglaVinculo.ResultadoRegla ejecutarClusterDesapariciones() {
        return reglaClusterDesapariciones.ejecutar();
    }
}