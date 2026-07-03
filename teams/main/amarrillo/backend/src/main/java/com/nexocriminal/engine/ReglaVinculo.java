package com.nexocriminal.engine;

import com.nexocriminal.domain.alerta.Alerta;
import com.nexocriminal.domain.vinculo.Vinculo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Estrategia que representa una regla de deteccion de vinculos.
 * Cada regla (NodoLogistico, Escolta, CirculoConfianza, ModusOperandi)
 * implementa esta interfaz.
 */
public interface ReglaVinculo {

    String nombre();

    ResultadoRegla ejecutar();

    @Getter
    @AllArgsConstructor
    class ResultadoRegla {
        private final String reglaNombre;
        private final List<Vinculo> vinculosDetectados;
        private final List<Alerta> alertasGeneradas;
    }
}
