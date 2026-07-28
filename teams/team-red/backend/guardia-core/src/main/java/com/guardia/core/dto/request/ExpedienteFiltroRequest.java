package com.guardia.core.dto.request;

import java.time.LocalDate;
import java.util.List;

public record ExpedienteFiltroRequest(
        List<String> tiposDelito,
        String municipio,
        String colonia,
        Double latitud,
        Double longitud,
        Double radioKm,
        LocalDate fechaDesde,
        LocalDate fechaHasta
) {
}