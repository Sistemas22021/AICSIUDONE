package naranja.custodia_360.models;

import java.util.List;

public record TestimonyHistoryDTO(
        String testimonyId,          // Usamos el UUID de la carpeta como identificador temporal
        List<String> availableFiles  // Lista dinámica de archivos en disco

    /* // TODO: Descomentar cuando la DB esté implementada
    Long id,
    String caseCode,
    LocalDateTime createdAt,
    String status,
    Long audioDurationSeconds
    */
) {}