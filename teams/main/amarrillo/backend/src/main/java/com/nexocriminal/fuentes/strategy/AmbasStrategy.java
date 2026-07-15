package com.nexocriminal.fuentes.strategy;

import com.nexocriminal.fuentes.DatosPersonaExterna;
import com.nexocriminal.fuentes.EstrategiaBusqueda;
import com.nexocriminal.fuentes.FuenteDatosPersona;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy: usa AMBAS fuentes de forma SECUENCIAL CON PRIORIDAD.
 *
 * Consulta primero la fuente prioritaria. Si encuentra resultados, los
 * devuelve. Si NO encuentra, consulta la fuente secundaria como respaldo.
 */
@Slf4j
public class AmbasStrategy implements EstrategiaBusqueda {

    private final FuenteDatosPersona primaria;
    private final FuenteDatosPersona secundaria;

    public AmbasStrategy(FuenteDatosPersona primaria, FuenteDatosPersona secundaria) {
        this.primaria = primaria;
        this.secundaria = secundaria;
    }

    @Override
    public DatosPersonaExterna buscar(String cedula) {
        log.info("[AMBAS] Consultando fuente primaria: {}", primaria.nombre());
        DatosPersonaExterna resultado = primaria.buscarPorCedula(cedula);

        if (resultado != null && resultado.isEncontrado()) {
            log.info("[AMBAS] Encontrado en fuente primaria ({})", primaria.nombre());
            return resultado;
        }

        log.info("[AMBAS] Sin resultado en primaria. Consultando secundaria: {}", secundaria.nombre());
        return secundaria.buscarPorCedula(cedula);
    }

    @Override
    public String descripcion() {
        return "Ambas (prioridad: " + primaria.nombre() + " -> respaldo: " + secundaria.nombre() + ")";
    }
}