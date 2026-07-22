package com.nexocriminal.fuentes;

/**
 * Los 3 modos de operacion configurables por variable de entorno.
 */
public enum ModoFuente {
    CNE,    // Solo scraping al CNE
    API,    // Solo API del profesor
    AMBAS;  // Ambas (secuencial con prioridad)

    public static ModoFuente from(String valor) {
        if (valor == null || valor.isBlank()) return AMBAS;
        try {
            return ModoFuente.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AMBAS; // valor por defecto seguro
        }
    }
}