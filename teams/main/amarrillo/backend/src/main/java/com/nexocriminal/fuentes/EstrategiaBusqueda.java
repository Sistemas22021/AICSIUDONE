package com.nexocriminal.fuentes;

/**
 * PATRON STRATEGY.
 *
 * Define COMO se realiza la busqueda, permitiendo intercambiar el
 * comportamiento en runtime segun la configuracion:
 *  - SoloCneStrategy
 *  - SoloApiStrategy
 *  - AmbasStrategy
 *
 * El codigo que la usa (BuscadorPersonasService) no sabe ni le importa
 * cual estrategia concreta esta activa.
 */
public interface EstrategiaBusqueda {

    DatosPersonaExterna buscar(String cedula);

    /** Descripcion de la estrategia activa (para el endpoint de estado) */
    String descripcion();
}