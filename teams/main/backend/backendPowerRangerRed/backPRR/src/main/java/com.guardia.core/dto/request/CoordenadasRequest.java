package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * DTO de solicitud para coordenadas GPS.
 * Contiene latitud y longitud obligatorias usadas por ubicaciones y escenas.
 */
public class CoordenadasRequest {
    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
}
