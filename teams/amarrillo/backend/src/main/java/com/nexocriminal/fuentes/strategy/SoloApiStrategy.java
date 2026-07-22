package com.nexocriminal.fuentes.strategy;

import com.nexocriminal.fuentes.DatosPersonaExterna;
import com.nexocriminal.fuentes.EstrategiaBusqueda;
import com.nexocriminal.fuentes.FuenteDatosPersona;

/** Strategy: usa unicamente la API del profesor. */
public class SoloApiStrategy implements EstrategiaBusqueda {

    private final FuenteDatosPersona api;

    public SoloApiStrategy(FuenteDatosPersona api) {
        this.api = api;
    }

    @Override
    public DatosPersonaExterna buscar(String cedula) {
        return api.buscarPorCedula(cedula);
    }

    @Override
    public String descripcion() {
        return "Solo API del profesor";
    }
}