package com.guardia.core.dto.request;

import java.time.LocalTime;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReporteRequest {

    @NotNull
    private LocalDate fechaReporte;

    @NotNull
    private LocalTime horaReporte;

    @NotBlank
    private String nombreInvestigador;

}
