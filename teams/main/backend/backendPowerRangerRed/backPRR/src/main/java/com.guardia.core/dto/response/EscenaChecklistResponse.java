package com.guardia.core.dto.response;

import com.guardia.core.model.enums.PasoChecklist;

import java.time.LocalDateTime;

public record EscenaChecklistResponse(

        Long id,
        PasoChecklist paso,
        Integer orden,
        Boolean completado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaCierre

) {}