package com.nexocriminal.fuentes;

/**
 * PATRON ADAPTER (target interface).
 *
 * Define el contrato comun que toda fuente de datos externa debe cumplir,
 * sin importar como obtiene los datos internamente (scraping, API REST, etc).
 *
 * Cumple ISP: una sola responsabilidad, un solo metodo de negocio.
 */
public interface FuenteDatosPersona {

    /**
     * Busca los datos de una persona por su cedula.
     * Si no encuentra nada o falla, devuelve un DatosPersonaExterna
     * con encontrado=false (nunca lanza excepcion hacia afuera).
     */
    DatosPersonaExterna buscarPorCedula(String cedula);

    /** Identificador legible de la fuente (para logs y trazabilidad) */
    String nombre();
}