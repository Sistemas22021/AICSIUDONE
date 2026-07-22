package com.nexocriminal.fuentes.strategy;

import com.nexocriminal.fuentes.DatosPersonaExterna;
import com.nexocriminal.fuentes.EstrategiaBusqueda;
import com.nexocriminal.fuentes.FuenteDatosPersona;

/** Strategy: usa unicamente la fuente CNE. */
public class SoloCneStrategy implements EstrategiaBusqueda {

    private final FuenteDatosPersona cne;

    public SoloCneStrategy(FuenteDatosPersona cne) {
        this.cne = cne;
    }

    @Override
    public DatosPersonaExterna buscar(String cedula) {
        return cne.buscarPorCedula(cedula);
    }

    @Override
    public String descripcion() {
        return "Solo CNE (scraping)";
    }
}