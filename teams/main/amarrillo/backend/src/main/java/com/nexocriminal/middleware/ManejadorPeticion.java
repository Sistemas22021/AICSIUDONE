package com.nexocriminal.middleware;

/**
 * PATRON CHAIN OF RESPONSIBILITY (handler abstracto).
 *
 * Cada eslabon de la cadena extiende esta clase. Procesa el contexto y
 * decide si pasa al siguiente eslabon (manejarSiguiente) o corta la cadena
 * (si rechaza la peticion).
 */
public abstract class ManejadorPeticion {

    private ManejadorPeticion siguiente;

    /** Encadena el proximo eslabon y lo devuelve (para encadenar fluido). */
    public ManejadorPeticion enlazarCon(ManejadorPeticion siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /** Cada eslabon implementa su logica de verificacion. */
    public abstract void manejar(ContextoPeticion contexto);

    /** Pasa el control al siguiente eslabon, si existe y no fue rechazado. */
    protected void manejarSiguiente(ContextoPeticion contexto) {
        if (contexto.isRechazado()) return; // la cadena se corta
        if (siguiente != null) {
            siguiente.manejar(contexto);
        }
    }
}