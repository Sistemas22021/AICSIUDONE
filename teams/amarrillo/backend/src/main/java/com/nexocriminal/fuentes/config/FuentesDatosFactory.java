package com.nexocriminal.fuentes.config;

import com.nexocriminal.fuentes.EstrategiaBusqueda;
import com.nexocriminal.fuentes.FuenteDatosPersona;
import com.nexocriminal.fuentes.ModoFuente;
import com.nexocriminal.fuentes.adapter.ApiProfesorAdapter;
import com.nexocriminal.fuentes.adapter.CneScrapingAdapter;
import com.nexocriminal.fuentes.strategy.AmbasStrategy;
import com.nexocriminal.fuentes.strategy.SoloApiStrategy;
import com.nexocriminal.fuentes.strategy.SoloCneStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PATRON FACTORY (Factory Method integrado con Spring).
 *
 * Lee la configuracion (variable de entorno nexo.fuentes.modo) y produce
 * la EstrategiaBusqueda concreta que se inyectara en todo el sistema.
 *
 * Asi, cambiar el modo de operacion (CNE | API | AMBAS) NO requiere tocar
 * codigo: solo cambiar una variable de entorno y reiniciar.
 */
@Configuration
@Slf4j
public class FuentesDatosFactory {

    @Bean
    public EstrategiaBusqueda estrategiaBusqueda(
            CneScrapingAdapter cne,
            ApiProfesorAdapter api,
            @Value("${nexo.fuentes.modo:AMBAS}") String modoStr,
            @Value("${nexo.fuentes.prioridad:CNE}") String prioridad) {

        ModoFuente modo = ModoFuente.from(modoStr);
        log.info("===========================================");
        log.info("[FUENTES] Modo de fuentes de datos: {}", modo);
        log.info("===========================================");

        return switch (modo) {
            case CNE -> new SoloCneStrategy(cne);
            case API -> new SoloApiStrategy(api);
            case AMBAS -> {
                // Secuencial con prioridad configurable
                FuenteDatosPersona primaria = "API".equalsIgnoreCase(prioridad) ? api : cne;
                FuenteDatosPersona secundaria = "API".equalsIgnoreCase(prioridad) ? cne : api;
                yield new AmbasStrategy(primaria, secundaria);
            }
        };
    }
}