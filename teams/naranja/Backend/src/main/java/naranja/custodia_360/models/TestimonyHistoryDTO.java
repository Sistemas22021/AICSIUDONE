package naranja.custodia_360.models;

public record TestimonyHistoryDTO(
        String sessionId,
        String cedula,
        String caseNumber,
        boolean hasAudio,
        boolean hasOriginalText,
        boolean hasModifiedText
) {}