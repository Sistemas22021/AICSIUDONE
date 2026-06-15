package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * DTO simple para representar un sub-delito (solo nombre).
 */
public class SubDelitoRequest {
    @NotBlank(message = "El nombre del sub-delito es obligatorio")
    private String nombre;
}
