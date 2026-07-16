package com.nexocriminal.fuentes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado NORMALIZADO de cualquier fuente de datos externa.
 * Cada Adapter traduce su formato propio a esta estructura comun.
 * (Patron Adapter: este es el "idioma comun" que entiende el sistema)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosPersonaExterna {

    private String cedula;
    private String nombreCompleto;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    // Datos geograficos (tipicos del CNE)
    private String estado;
    private String municipio;
    private String parroquia;
    private String centroVotacion;

    // Trazabilidad: de que fuente provienen estos datos
    private String fuente;

    // Indica si la busqueda encontro resultados
    private boolean encontrado;

    /** Factory method para resultado vacio */
    public static DatosPersonaExterna noEncontrado(String cedula, String fuente) {
        return DatosPersonaExterna.builder()
                .cedula(cedula)
                .fuente(fuente)
                .encontrado(false)
                .build();
    }
}