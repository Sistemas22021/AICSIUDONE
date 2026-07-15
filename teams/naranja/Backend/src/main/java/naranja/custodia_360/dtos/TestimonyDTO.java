package naranja.custodia_360.dtos;

import naranja.custodia_360.models.Testimony;

public record TestimonyDTO(
        String sessionId,
        String cedula,
        String caseNumber,
        String testimonySummary
) {
    public TestimonyDTO(Testimony testimony, String aiSummary) {
        this(
                testimony.getSessionId(),
                testimony.getCedula(),
                testimony.getCaseNumber(),
                aiSummary
        );
    }
}
