package com.guardia.core.dto.response;

import com.guardia.core.model.enums.PasoChecklist;

import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa un paso del checklist de una escena.
 */
public record EscenaChecklistResponse(

        Long id,
        PasoChecklist paso,
        Integer orden,
        Boolean completado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaCierre

) {}