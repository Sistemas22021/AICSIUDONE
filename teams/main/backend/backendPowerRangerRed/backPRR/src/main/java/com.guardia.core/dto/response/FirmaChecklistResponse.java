package com.guardia.core.dto.response;

import com.guardia.core.model.enums.PasoChecklist;
import java.time.LocalDateTime;

public record FirmaChecklistResponse(
        Long id,
        Long pasoChecklistId,
        PasoChecklist paso,
        Long investigadorId,
        String investigadorNombre,
        LocalDateTime timestampFirma,
        Boolean exitoso,
        String motivoFallo
) {}