package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FirmarPasoRequest(

        @NotNull(message = "El ID del investigador es obligatorio")
        Long investigadorId,

        @NotBlank(message = "El PIN no puede estar vacío")
        String pin

) {
        public Long getInvestigadorId() { return this.investigadorId; }
        public String getPin() { return this.pin; }
}